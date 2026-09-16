import express from 'express';
import {
  getProducts,
  getProductById,
  getFeaturedProducts,
  getBestSellers,
  getDealsOfDay,
  getCategories,
  getBanners,
  getProductReviews,
  addProductReview
} from '../controllers/productController.js';
import { authenticate } from '../middleware/auth.js';

const router = express.Router();

router.get('/', getProducts);
router.get('/featured', getFeaturedProducts);
router.get('/best-sellers', getBestSellers);
router.get('/deals-of-day', getDealsOfDay);
router.get('/categories', getCategories);
router.get('/banners', getBanners);
router.get('/:id', getProductById);
router.get('/:id/reviews', getProductReviews);
router.post('/:id/reviews', authenticate, addProductReview);

export default router;
