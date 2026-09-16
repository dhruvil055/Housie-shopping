import { Product } from '../models/Product.js';
import { AppError } from '../middleware/errorHandler.js';

/**
 * Check real-time stock availability for items
 * POST /api/v1/inventory/check
 * Body: { items: [{ productId, variantId, quantity }] }
 */
export const checkStock = async (req, res, next) => {
  try {
    const { items } = req.body;
    if (!Array.isArray(items) || items.length === 0) {
      throw new AppError('items must be a non-empty array of { productId, quantity }', 400, 'INVALID_INPUT');
    }

    const results = [];
    let allAvailable = true;

    for (const item of items) {
      const product = await Product.findById(item.productId);
      if (!product || !product.isActive) {
        results.push({
          productId: item.productId,
          available: false,
          reason: 'Product not found or inactive',
          availableQuantity: 0,
          requestedQuantity: item.quantity
        });
        allAvailable = false;
        continue;
      }

      const availableStock = Math.max(0, product.stock - (product.reservedStock || 0));
      const requestedQuantity = Number(item.quantity) || 1;
      const hasStock = availableStock >= requestedQuantity;

      if (!hasStock) {
        allAvailable = false;
      }

      results.push({
        productId: product._id.toString(),
        sku: product.sku,
        title: product.title,
        price: product.price,
        available: hasStock,
        availableQuantity: availableStock,
        requestedQuantity
      });
    }

    res.status(200).json({
      success: true,
      data: {
        allAvailable,
        items: results
      }
    });
  } catch (error) {
    next(error);
  }
};

/**
 * Reserve stock temporarily during order placement
 * POST /api/v1/inventory/reserve
 * Body: { items: [{ productId, quantity }] }
 */
export const reserveStock = async (req, res, next) => {
  try {
    const { items } = req.body;
    if (!Array.isArray(items) || items.length === 0) {
      throw new AppError('items must be a non-empty array', 400, 'INVALID_INPUT');
    }

    const reservedItems = [];

    for (const item of items) {
      const requestedQuantity = Number(item.quantity) || 1;
      
      // Atomically check and reserve stock
      const updatedProduct = await Product.findOneAndUpdate(
        {
          _id: item.productId,
          $expr: { $gte: [{ $subtract: ['$stock', { $ifNull: ['$reservedStock', 0] }] }, requestedQuantity] }
        },
        { $inc: { reservedStock: requestedQuantity } },
        { new: true }
      );

      if (!updatedProduct) {
        // Rollback already reserved items
        for (const rollbackItem of reservedItems) {
          await Product.findByIdAndUpdate(rollbackItem.productId, {
            $inc: { reservedStock: -rollbackItem.quantity }
          });
        }

        throw new AppError(
          `Insufficient stock to reserve item ${item.productId}`,
          400,
          'INSUFFICIENT_STOCK'
        );
      }

      reservedItems.push({
        productId: item.productId,
        quantity: requestedQuantity
      });
    }

    res.status(200).json({
      success: true,
      message: 'Stock reserved successfully',
      data: {
        reservedItems,
        expiresInSeconds: 900 // 15 minutes hold
      }
    });
  } catch (error) {
    next(error);
  }
};

/**
 * Release reserved stock (e.g., checkout abandoned or payment failed)
 * POST /api/v1/inventory/release
 * Body: { items: [{ productId, quantity }] }
 */
export const releaseStock = async (req, res, next) => {
  try {
    const { items } = req.body;
    if (!Array.isArray(items) || items.length === 0) {
      throw new AppError('items must be a non-empty array', 400, 'INVALID_INPUT');
    }

    for (const item of items) {
      const quantity = Number(item.quantity) || 0;
      if (quantity > 0) {
        await Product.findByIdAndUpdate(item.productId, {
          $inc: { reservedStock: -quantity }
        });
      }
    }

    res.status(200).json({
      success: true,
      message: 'Reserved stock released successfully'
    });
  } catch (error) {
    next(error);
  }
};

/**
 * Get low stock products
 * GET /api/v1/inventory/low-stock?threshold=10
 */
export const getLowStockItems = async (req, res, next) => {
  try {
    const threshold = parseInt(req.query.threshold, 10) || 10;
    const page = parseInt(req.query.page, 10) || 1;
    const limit = parseInt(req.query.limit, 10) || 20;

    const query = {
      isActive: true,
      $expr: {
        $lte: [{ $subtract: ['$stock', { $ifNull: ['$reservedStock', 0] }] }, threshold]
      }
    };

    const total = await Product.countDocuments(query);
    const products = await Product.find(query)
      .select('sku title brand price stock reservedStock categoryName')
      .skip((page - 1) * limit)
      .limit(limit)
      .lean();

    res.status(200).json({
      success: true,
      data: {
        items: products.map(p => ({
          ...p,
          availableStock: Math.max(0, p.stock - (p.reservedStock || 0))
        })),
        pagination: {
          total,
          page,
          pages: Math.ceil(total / limit),
          threshold
        }
      }
    });
  } catch (error) {
    next(error);
  }
};
