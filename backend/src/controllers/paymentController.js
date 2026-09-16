import crypto from 'crypto';
import { Order } from '../models/Order.js';
import { Notification } from '../models/Notification.js';
import { AppError } from '../middleware/errorHandler.js';
import { logger } from '../config/logger.js';
import { ORDER_STATUS, PAYMENT_STATUS } from '../config/constants.js';

const RAZORPAY_SECRET = process.env.RAZORPAY_KEY_SECRET || 'rzp_secret_test_key_housie_2026';
const RAZORPAY_WEBHOOK_SECRET = process.env.RAZORPAY_WEBHOOK_SECRET || 'rzp_webhook_secret_housie_2026';

/**
 * Verify Razorpay payment signature
 * POST /api/v1/payments/verify
 */
export const verifyPaymentSignature = async (req, res, next) => {
  try {
    const { orderId, razorpayOrderId, razorpayPaymentId, razorpaySignature } = req.body;

    if (!orderId || !razorpayOrderId || !razorpayPaymentId || !razorpaySignature) {
      throw new AppError('Missing payment verification parameters', 400, 'INVALID_PAYMENT_DATA');
    }

    const order = await Order.findById(orderId);
    if (!order) {
      throw new AppError('Order not found', 404, 'ORDER_NOT_FOUND');
    }

    // Verify HMAC-SHA256 signature
    const body = `${razorpayOrderId}|${razorpayPaymentId}`;
    const expectedSignature = crypto
      .createHmac('sha256', RAZORPAY_SECRET)
      .update(body)
      .digest('hex');

    const isValid = expectedSignature === razorpaySignature;

    if (!isValid) {
      order.paymentStatus = PAYMENT_STATUS.FAILED;
      await order.save();
      throw new AppError('Payment signature verification failed. Potential tampering detected.', 400, 'INVALID_SIGNATURE');
    }

    // Mark order paid and confirmed
    order.paymentStatus = PAYMENT_STATUS.PAID;
    order.status = ORDER_STATUS.CONFIRMED;
    order.razorpayPaymentId = razorpayPaymentId;
    order.razorpaySignature = razorpaySignature;
    order.paidAt = new Date();

    order.timeline.push({
      status: ORDER_STATUS.CONFIRMED,
      title: 'Payment Confirmed',
      description: `Payment ₹${order.total} received successfully via Razorpay (${razorpayPaymentId}).`,
      timestamp: new Date()
    });

    await order.save();

    // Create user notification
    await Notification.create({
      userId: order.userId,
      title: 'Payment Confirmed',
      message: `Your payment of ₹${order.total} for order #${order.orderNumber} was successful.`,
      type: 'PAYMENT',
      data: { orderId: order._id.toString(), orderNumber: order.orderNumber }
    });

    res.status(200).json({
      success: true,
      message: 'Payment verified successfully and order confirmed.',
      data: {
        orderId: order._id.toString(),
        orderNumber: order.orderNumber,
        status: order.status,
        paymentStatus: order.paymentStatus
      }
    });
  } catch (error) {
    next(error);
  }
};

/**
 * Process Razorpay Webhook
 * POST /api/v1/payments/webhook
 */
export const handleRazorpayWebhook = async (req, res, next) => {
  try {
    const signature = req.headers['x-razorpay-signature'];
    const payload = JSON.stringify(req.body);

    if (signature) {
      const expectedSignature = crypto
        .createHmac('sha256', RAZORPAY_WEBHOOK_SECRET)
        .update(payload)
        .digest('hex');

      if (expectedSignature !== signature) {
        logger.warn('Razorpay webhook signature mismatch detected');
        return res.status(400).json({ success: false, message: 'Invalid webhook signature' });
      }
    }

    const { event, payload: eventPayload } = req.body;
    logger.info(`Processing Razorpay webhook event: ${event}`);

    if (event === 'payment.captured') {
      const paymentEntity = eventPayload?.payment?.entity;
      const razorpayOrderId = paymentEntity?.order_id;

      if (razorpayOrderId) {
        const order = await Order.findOne({ razorpayOrderId });
        if (order && order.paymentStatus !== PAYMENT_STATUS.PAID) {
          order.paymentStatus = PAYMENT_STATUS.PAID;
          order.status = ORDER_STATUS.CONFIRMED;
          order.razorpayPaymentId = paymentEntity.id;
          order.paidAt = new Date();
          await order.save();
        }
      }
    } else if (event === 'payment.failed') {
      const paymentEntity = eventPayload?.payment?.entity;
      const razorpayOrderId = paymentEntity?.order_id;

      if (razorpayOrderId) {
        const order = await Order.findOne({ razorpayOrderId });
        if (order && order.paymentStatus !== PAYMENT_STATUS.PAID) {
          order.paymentStatus = PAYMENT_STATUS.FAILED;
          await order.save();
        }
      }
    }

    res.status(200).json({ success: true, status: 'Webhook processed' });
  } catch (error) {
    logger.error(`Webhook processing error: ${error.message}`);
    res.status(200).json({ success: false, error: error.message }); // Always 200 to acknowledge webhook reception
  }
};

/**
 * Get available payment methods
 * GET /api/v1/payments/methods
 */
export const getPaymentMethods = async (req, res) => {
  res.status(200).json({
    success: true,
    data: {
      methods: [
        {
          id: 'RAZORPAY',
          name: 'Razorpay Online Payment',
          description: 'UPI, Credit/Debit Cards, NetBanking, Wallets',
          enabled: true
        },
        {
          id: 'COD',
          name: 'Cash on Delivery',
          description: 'Pay cash when materials are delivered to your site',
          enabled: true,
          extraFee: 0
        }
      ],
      currency: 'INR',
      razorpayKeyId: process.env.RAZORPAY_KEY_ID || 'rzp_test_placeholder'
    }
  });
};
