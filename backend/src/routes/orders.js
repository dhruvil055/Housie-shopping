import express from 'express';
import {
  createCheckoutOrder,
  verifyPayment,
  getCustomerOrders,
  getOrderById,
  cancelOrder,
  trackOrder
} from '../controllers/orderController.js';
import { authenticate } from '../middleware/auth.js';
import { checkIdempotency } from '../middleware/idempotency.js';

const router = express.Router();

router.use(authenticate);

// Orders
router.get('/', getCustomerOrders);
router.post('/create', checkIdempotency, createCheckoutOrder);
router.get('/:id', getOrderById);
router.post('/:id/cancel', cancelOrder);
router.get('/:id/track', trackOrder);

// Payment Verification
router.post('/verify-payment', verifyPayment);

export default router;
