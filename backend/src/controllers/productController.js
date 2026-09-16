import { Product } from '../models/Product.js';
import { Category } from '../models/Category.js';
import { Banner } from '../models/Banner.js';
import { Review } from '../models/Review.js';
import { Order } from '../models/Order.js';
import { AppError } from '../middleware/errorHandler.js';

export const getProducts = async (req, res, next) => {
  try {
    const {
      search,
      category,
      brand,
      minPrice,
      maxPrice,
      minRating,
      inStock,
      sort,
      page = 1,
      limit = 20
    } = req.query;

    const query = { isActive: true };

    if (search && search.trim()) {
      query.$text = { $search: search.trim() };
    }

    if (category && category !== 'all') {
      query.$or = [{ categoryId: category }, { categoryName: new RegExp(category, 'i') }];
    }

    if (brand && brand !== 'all') {
      query.brand = new RegExp(`^${brand}$`, 'i');
    }

    if (minPrice || maxPrice) {
      query.price = {};
      if (minPrice) query.price.$gte = Number(minPrice);
      if (maxPrice) query.price.$lte = Number(maxPrice);
    }

    if (minRating) {
      query.rating = { $gte: Number(minRating) };
    }

    if (inStock === 'true') {
      query.stock = { $gt: 0 };
    }

    // Sort options
    let sortOption = { createdAt: -1 };
    if (sort === 'price_asc') sortOption = { price: 1 };
    else if (sort === 'price_desc') sortOption = { price: -1 };
    else if (sort === 'rating') sortOption = { rating: -1 };
    else if (sort === 'popularity' || sort === 'best_seller') sortOption = { isBestSeller: -1, reviewCount: -1 };

    const pageNum = Math.max(1, parseInt(page, 10));
    const limitNum = Math.min(100, Math.max(1, parseInt(limit, 10)));
    const skip = (pageNum - 1) * limitNum;

    const [products, total] = await Promise.all([
      Product.find(query).sort(sortOption).skip(skip).limit(limitNum),
      Product.countDocuments(query)
    ]);

    res.status(200).json({
      success: true,
      data: products,
      meta: {
        page: pageNum,
        limit: limitNum,
        total,
        totalPages: Math.ceil(total / limitNum)
      }
    });
  } catch (error) {
    next(error);
  }
};

export const getProductById = async (req, res, next) => {
  try {
    const { id } = req.params;
    let product;

    // Check if id is valid ObjectId or SKU
    if (id.match(/^[0-9a-fA-F]{24}$/)) {
      product = await Product.findById(id);
    } else {
      product = await Product.findOne({ $or: [{ sku: id.toUpperCase() }, { _id: id }] });
    }

    if (!product) {
      throw new AppError('Product not found.', 404, 'PRODUCT_NOT_FOUND');
    }

    // Fetch up to 4 related products from same category
    const relatedProducts = await Product.find({
      categoryId: product.categoryId,
      _id: { $ne: product._id },
      isActive: true
    }).limit(4);

    res.status(200).json({
      success: true,
      data: {
        ...product.toObject(),
        relatedProducts
      }
    });
  } catch (error) {
    next(error);
  }
};

export const getFeaturedProducts = async (req, res, next) => {
  try {
    const products = await Product.find({ isFeatured: true, isActive: true }).limit(10);
    res.status(200).json({ success: true, data: products });
  } catch (error) {
    next(error);
  }
};

export const getBestSellers = async (req, res, next) => {
  try {
    const products = await Product.find({ isBestSeller: true, isActive: true }).limit(10);
    res.status(200).json({ success: true, data: products });
  } catch (error) {
    next(error);
  }
};

export const getDealsOfDay = async (req, res, next) => {
  try {
    const products = await Product.find({ isDealOfDay: true, isActive: true }).limit(10);
    res.status(200).json({ success: true, data: products });
  } catch (error) {
    next(error);
  }
};

export const getCategories = async (req, res, next) => {
  try {
    const categories = await Category.find({ isActive: true }).sort({ displayOrder: 1 });
    res.status(200).json({ success: true, data: categories });
  } catch (error) {
    next(error);
  }
};

export const getBanners = async (req, res, next) => {
  try {
    const banners = await Banner.find({ isActive: true }).sort({ displayOrder: 1 });
    res.status(200).json({ success: true, data: banners });
  } catch (error) {
    next(error);
  }
};

export const getProductReviews = async (req, res, next) => {
  try {
    const { id } = req.params;
    const reviews = await Review.find({ productId: id, isApproved: true }).sort({ createdAt: -1 });
    res.status(200).json({ success: true, data: reviews });
  } catch (error) {
    next(error);
  }
};

export const addProductReview = async (req, res, next) => {
  try {
    const { id: productId } = req.params;
    const { rating, title, comment } = req.body;

    // Check if user has purchased this product
    const pastOrder = await Order.findOne({
      userId: req.user._id,
      'items.productId': productId,
      orderStatus: { $in: ['DELIVERED', 'SHIPPED', 'CONFIRMED'] }
    });

    const isVerifiedPurchase = !!pastOrder;

    const review = new Review({
      productId,
      userId: req.user._id,
      userName: req.user.name,
      rating,
      title,
      comment,
      isVerifiedPurchase,
      isApproved: true
    });

    await review.save();

    // Recalculate product average rating
    const allReviews = await Review.find({ productId, isApproved: true });
    const avgRating = allReviews.reduce((sum, r) => sum + r.rating, 0) / allReviews.length;
    await Product.findByIdAndUpdate(productId, {
      rating: parseFloat(avgRating.toFixed(1)),
      reviewCount: allReviews.length
    });

    res.status(201).json({
      success: true,
      message: 'Review posted successfully.',
      data: review
    });
  } catch (error) {
    next(error);
  }
};
