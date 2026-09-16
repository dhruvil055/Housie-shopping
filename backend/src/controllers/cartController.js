import { Cart } from '../models/Cart.js';
import { Product } from '../models/Product.js';
import { Coupon } from '../models/Coupon.js';
import { AppError } from '../middleware/errorHandler.js';
import { PRICING_RULES } from '../config/constants.js';

export const calculateCartTotals = async (cart) => {
  if (!cart || !cart.items || cart.items.length === 0) {
    return {
      items: [],
      subtotal: 0,
      totalMrp: 0,
      totalDiscount: 0,
      couponDiscount: 0,
      appliedCouponCode: null,
      taxAmount: 0,
      deliveryFee: 0,
      grandTotal: 0,
      totalSavings: 0
    };
  }

  const populatedItems = [];
  let subtotal = 0;
  let totalMrp = 0;

  for (const item of cart.items) {
    const product = await Product.findById(item.productId);
    if (!product || !product.isActive) continue;

    let unitPrice = product.price;
    let unitMrp = product.mrp;
    let availableStock = product.stock - (product.reservedStock || 0);
    let variantDetails = null;

    if (item.variantId) {
      const v = product.variants.id(item.variantId);
      if (v) {
        unitPrice = v.price;
        unitMrp = v.mrp;
        availableStock = v.stock;
        variantDetails = {
          id: v._id.toString(),
          name: v.name,
          price: v.price,
          mrp: v.mrp,
          stock: v.stock
        };
      }
    }

    const itemSubtotal = unitPrice * item.quantity;
    const itemTotalMrp = unitMrp * item.quantity;

    if (!item.savedForLater) {
      subtotal += itemSubtotal;
      totalMrp += itemTotalMrp;
    }

    populatedItems.push({
      id: item._id.toString(),
      productId: product._id.toString(),
      title: product.title,
      imageUrl: product.images[0] || '',
      brand: product.brand,
      categoryName: product.categoryName,
      unitPrice,
      unitMrp,
      quantity: item.quantity,
      subtotal: itemSubtotal,
      totalMrp: itemTotalMrp,
      availableStock,
      isOutOfStock: availableStock <= 0,
      selectedVariant: variantDetails,
      savedForLater: item.savedForLater
    });
  }

  const totalDiscount = Math.max(0, totalMrp - subtotal);
  let couponDiscount = 0;
  let validCouponCode = null;

  if (cart.appliedCoupon) {
    const coupon = await Coupon.findOne({ code: cart.appliedCoupon.toUpperCase(), isActive: true });
    if (coupon && coupon.isValid(subtotal)) {
      validCouponCode = coupon.code;
      if (coupon.discountType === 'PERCENTAGE') {
        const calculated = (subtotal * coupon.discountValue) / 100.0;
        couponDiscount = coupon.maxDiscountAmount ? Math.min(calculated, coupon.maxDiscountAmount) : calculated;
      } else {
        couponDiscount = Math.min(coupon.discountValue, subtotal);
      }
    } else {
      // Clear invalid coupon from cart
      cart.appliedCoupon = null;
      await cart.save();
    }
  }

  const taxableAmount = Math.max(0, subtotal - couponDiscount);
  const taxAmount = Math.round(taxableAmount * PRICING_RULES.GST_RATE * 100) / 100;
  const activeItemsCount = populatedItems.filter(i => !i.savedForLater).length;
  const deliveryFee = subtotal >= PRICING_RULES.FREE_DELIVERY_THRESHOLD || activeItemsCount === 0 ? 0 : PRICING_RULES.DEFAULT_DELIVERY_FEE;
  const grandTotal = Math.round((taxableAmount + taxAmount + deliveryFee) * 100) / 100;

  return {
    items: populatedItems,
    subtotal: Math.round(subtotal * 100) / 100,
    totalMrp: Math.round(totalMrp * 100) / 100,
    totalDiscount: Math.round(totalDiscount * 100) / 100,
    couponDiscount: Math.round(couponDiscount * 100) / 100,
    appliedCouponCode: validCouponCode,
    taxAmount,
    deliveryFee,
    grandTotal,
    totalSavings: Math.round((totalDiscount + couponDiscount) * 100) / 100
  };
};

export const getCart = async (req, res, next) => {
  try {
    let cart = await Cart.findOne({ userId: req.user._id });
    if (!cart) {
      cart = await Cart.create({ userId: req.user._id, items: [] });
    }
    const summary = await calculateCartTotals(cart);
    res.status(200).json({
      success: true,
      data: summary
    });
  } catch (error) {
    next(error);
  }
};

export const addToCart = async (req, res, next) => {
  try {
    const { productId, variantId, quantity = 1 } = req.body;
    const qty = Math.max(1, parseInt(quantity, 10));

    const product = await Product.findById(productId);
    if (!product || !product.isActive) {
      throw new AppError('Product not found or currently unavailable.', 404, 'PRODUCT_NOT_FOUND');
    }

    let availableStock = product.stock - (product.reservedStock || 0);
    if (variantId) {
      const v = product.variants.id(variantId);
      if (!v) throw new AppError('Specified variant not found.', 404, 'VARIANT_NOT_FOUND');
      availableStock = v.stock;
    }

    if (availableStock <= 0) {
      throw new AppError('This product is currently out of stock.', 400, 'OUT_OF_STOCK');
    }

    let cart = await Cart.findOne({ userId: req.user._id });
    if (!cart) {
      cart = new Cart({ userId: req.user._id, items: [] });
    }

    const existingIndex = cart.items.findIndex(
      (item) => item.productId.toString() === productId && (item.variantId || null) === (variantId || null)
    );

    if (existingIndex >= 0) {
      const newQty = cart.items[existingIndex].quantity + qty;
      if (newQty > availableStock) {
        throw new AppError(`Cannot add more. Only ${availableStock} units available in stock.`, 400, 'INSUFFICIENT_STOCK');
      }
      cart.items[existingIndex].quantity = newQty;
      cart.items[existingIndex].savedForLater = false;
    } else {
      if (qty > availableStock) {
        throw new AppError(`Only ${availableStock} units available in stock.`, 400, 'INSUFFICIENT_STOCK');
      }
      cart.items.push({ productId, variantId, quantity: qty, savedForLater: false });
    }

    await cart.save();
    const summary = await calculateCartTotals(cart);
    res.status(200).json({
      success: true,
      message: 'Added to cart.',
      data: summary
    });
  } catch (error) {
    next(error);
  }
};

export const updateQuantity = async (req, res, next) => {
  try {
    const { cartItemId } = req.params;
    const { quantity } = req.body;
    const qty = parseInt(quantity, 10);

    const cart = await Cart.findOne({ userId: req.user._id });
    if (!cart) throw new AppError('Cart not found.', 404, 'CART_NOT_FOUND');

    const item = cart.items.id(cartItemId);
    if (!item) throw new AppError('Cart item not found.', 404, 'ITEM_NOT_FOUND');

    if (qty <= 0) {
      item.deleteOne();
    } else {
      // Validate stock
      const product = await Product.findById(item.productId);
      let available = product ? product.stock - (product.reservedStock || 0) : 0;
      if (item.variantId && product) {
        const v = product.variants.id(item.variantId);
        if (v) available = v.stock;
      }

      if (qty > available) {
        throw new AppError(`Only ${available} units available in stock.`, 400, 'INSUFFICIENT_STOCK');
      }
      item.quantity = qty;
    }

    await cart.save();
    const summary = await calculateCartTotals(cart);
    res.status(200).json({
      success: true,
      data: summary
    });
  } catch (error) {
    next(error);
  }
};

export const removeFromCart = async (req, res, next) => {
  try {
    const { cartItemId } = req.params;
    const cart = await Cart.findOne({ userId: req.user._id });
    if (cart) {
      const item = cart.items.id(cartItemId);
      if (item) item.deleteOne();
      await cart.save();
    }
    const summary = await calculateCartTotals(cart);
    res.status(200).json({
      success: true,
      message: 'Item removed from cart.',
      data: summary
    });
  } catch (error) {
    next(error);
  }
};

export const clearCart = async (req, res, next) => {
  try {
    const cart = await Cart.findOne({ userId: req.user._id });
    if (cart) {
      cart.items = [];
      cart.appliedCoupon = null;
      await cart.save();
    }
    const summary = await calculateCartTotals(cart);
    res.status(200).json({
      success: true,
      message: 'Cart cleared.',
      data: summary
    });
  } catch (error) {
    next(error);
  }
};

export const applyCoupon = async (req, res, next) => {
  try {
    const { couponCode } = req.body;
    if (!couponCode) throw new AppError('Coupon code is required.', 400, 'COUPON_REQUIRED');

    const coupon = await Coupon.findOne({ code: couponCode.toUpperCase().trim(), isActive: true });
    if (!coupon) {
      throw new AppError('Invalid or expired coupon code.', 400, 'INVALID_COUPON');
    }

    let cart = await Cart.findOne({ userId: req.user._id });
    if (!cart || cart.items.length === 0) {
      throw new AppError('Cart is empty. Add items before applying coupon.', 400, 'CART_EMPTY');
    }

    cart.appliedCoupon = coupon.code;
    await cart.save();

    const summary = await calculateCartTotals(cart);
    if (!summary.appliedCouponCode) {
      throw new AppError(`Order subtotal must be at least ₹${coupon.minOrderAmount} to apply ${coupon.code}.`, 400, 'MIN_AMOUNT_NOT_MET');
    }

    res.status(200).json({
      success: true,
      message: `Coupon ${coupon.code} applied successfully!`,
      data: summary
    });
  } catch (error) {
    next(error);
  }
};

export const removeCoupon = async (req, res, next) => {
  try {
    let cart = await Cart.findOne({ userId: req.user._id });
    if (cart) {
      cart.appliedCoupon = null;
      await cart.save();
    }
    const summary = await calculateCartTotals(cart);
    res.status(200).json({
      success: true,
      message: 'Coupon removed.',
      data: summary
    });
  } catch (error) {
    next(error);
  }
};
