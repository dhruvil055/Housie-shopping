import express from 'express';
import {
  verifyPaymentSignature,
  handleRazorpayWebhook,
  getPaymentMethods
} from '../controllers/paymentController.js';
import { authenticate } from '../middleware/auth.js';

const router = express.Router();

// Public webhook listener
router.post('/webhook', handleRazorpayWebhook);

// Payment methods discovery
router.get('/methods', getPaymentMethods);

// Verify payment signature
router.post('/verify', authenticate, verifyPaymentSignature);

export default router;
