import crypto from 'crypto';
import { Order } from '../models/Order.js';
import { Cart } from '../models/Cart.js';
import { Product } from '../models/Product.js';
import { calculateCartTotals } from './cartController.js';
import { AppError } from '../middleware/errorHandler.js';
import { ORDER_STATUS, PAYMENT_STATUS, PAYMENT_METHOD } from '../config/constants.js';

export const createCheckoutOrder = async (req, res, next) => {
  try {
    const { address, paymentMethod = PAYMENT_METHOD.RAZORPAY } = req.body;
    const idempotencyKey = req.headers['x-idempotency-key'];

    if (idempotencyKey) {
      const existing = await Order.findOne({ idempotencyKey });
      if (existing) {
        return res.status(200).json({
          success: true,
          message: 'Order already created.',
          data: existing
        });
      }
    }

    if (!address || !address.houseFlat || !address.city) {
      throw new AppError('A valid delivery address is required.', 400, 'INVALID_ADDRESS');
    }

    const cart = await Cart.findOne({ userId: req.user._id });
    if (!cart || cart.items.length === 0) {
      throw new AppError('Cart is empty.', 400, 'CART_EMPTY');
    }

    // Revalidate stock and calculate final server totals
    const cartSummary = await calculateCartTotals(cart);
    const activeItems = cartSummary.items.filter(i => !i.savedForLater);

    if (activeItems.length === 0) {
      throw new AppError('No active items in cart to checkout.', 400, 'CART_EMPTY');
    }

    // Check stock for all items
    for (const item of activeItems) {
      if (item.availableStock < item.quantity) {
        throw new AppError(
          `Insufficient stock for '${item.title}'. Only ${item.availableStock} available.`,
          400,
          'INSUFFICIENT_STOCK'
        );
      }
    }

    // Reserve stock atomically
    for (const item of activeItems) {
      await Product.findByIdAndUpdate(item.productId, {
        $inc: { reservedStock: item.quantity }
      });
    }

    const orderNumber = `HS-${new Date().getFullYear()}-${Math.floor(100000 + Math.random() * 900000)}`;
    const isCOD = paymentMethod === PAYMENT_METHOD.COD;

    const orderItems = activeItems.map(i => ({
      productId: i.productId,
      title: i.title,
      imageUrl: i.imageUrl,
      variantName: i.selectedVariant ? i.selectedVariant.name : '',
      unitPrice: i.unitPrice,
      quantity: i.quantity,
      totalPrice: i.subtotal
    }));

    const initialTimeline = [
      {
        status: ORDER_STATUS.CONFIRMED,
        title: 'Order Placed',
        description: isCOD ? 'Order confirmed via Cash on Delivery' : 'Order received, pending payment',
        timestamp: new Date().toISOString(),
        isCompleted: true,
        isCurrent: true
      },
      {
        status: ORDER_STATUS.PACKED,
        title: 'Packing Material',
        description: 'Quality check and packing at local depot',
        timestamp: 'Pending',
        isCompleted: false,
        isCurrent: false
      },
      {
        status: ORDER_STATUS.SHIPPED,
        title: 'Dispatched',
        description: 'Handed over to fleet logistics',
        timestamp: 'Pending',
        isCompleted: false,
        isCurrent: false
      },
      {
        status: ORDER_STATUS.OUT_FOR_DELIVERY,
        title: 'Out for Delivery',
        description: 'Driver assigned for site delivery',
        timestamp: 'Pending',
        isCompleted: false,
        isCurrent: false
      },
      {
        status: ORDER_STATUS.DELIVERED,
        title: 'Delivered',
        description: 'Material delivered and inspected at construction site',
        timestamp: 'Pending',
        isCompleted: false,
        isCurrent: false
      }
    ];

    let razorpayOrderId = null;
    if (!isCOD) {
      // Create mock Razorpay order ID or real if credentials provided
      razorpayOrderId = `order_${Date.now()}_${Math.floor(Math.random() * 1000)}`;
    }

    const order = new Order({
      orderNumber,
      idempotencyKey,
      userId: req.user._id,
      customerName: address.fullName || req.user.name,
      customerEmail: req.user.email,
      customerPhone: address.phone || req.user.phone,
      items: orderItems,
      shippingAddress: address,
      paymentMethod,
      paymentStatus: isCOD ? PAYMENT_STATUS.PENDING : PAYMENT_STATUS.PENDING,
      orderStatus: isCOD ? ORDER_STATUS.CONFIRMED : ORDER_STATUS.PENDING_PAYMENT,
      razorpayOrderId,
      subtotal: cartSummary.subtotal,
      discount: cartSummary.totalDiscount,
      couponDiscount: cartSummary.couponDiscount,
      couponCode: cartSummary.appliedCouponCode,
      tax: cartSummary.taxAmount,
      deliveryFee: cartSummary.deliveryFee,
      totalAmount: cartSummary.grandTotal,
      estimatedDeliveryDate: 'Tomorrow by 5:00 PM',
      timeline: initialTimeline
    });

    await order.save();

    // If COD, finalize inventory immediately and clear cart
    if (isCOD) {
      for (const item of activeItems) {
        await Product.findByIdAndUpdate(item.productId, {
          $inc: { stock: -item.quantity, reservedStock: -item.quantity }
        });
      }
      cart.items = cart.items.filter(i => i.savedForLater);
      cart.appliedCoupon = null;
      await cart.save();
    }

    res.status(201).json({
      success: true,
      message: isCOD ? 'Order placed successfully!' : 'Payment order created.',
      data: {
        orderId: order._id.toString(),
        orderNumber: order.orderNumber,
        totalAmount: order.totalAmount,
        currency: 'INR',
        razorpayOrderId,
        razorpayKey: process.env.RAZORPAY_KEY_ID || 'rzp_test_placeholder',
        isCOD
      }
    });
  } catch (error) {
    next(error);
  }
};

export const verifyPayment = async (req, res, next) => {
  try {
    const { orderId, razorpayOrderId, razorpayPaymentId, razorpaySignature } = req.body;

    const order = await Order.findById(orderId);
    if (!order) {
      throw new AppError('Order not found.', 404, 'ORDER_NOT_FOUND');
    }

    if (order.paymentStatus === PAYMENT_STATUS.PAID) {
      return res.status(200).json({
        success: true,
        message: 'Order already marked as paid.',
        data: order
      });
    }

    // Verify cryptographic signature if secret is configured
    const keySecret = process.env.RAZORPAY_KEY_SECRET;
    if (keySecret && razorpayOrderId && razorpayPaymentId && razorpaySignature) {
      const generatedSignature = crypto
        .createHmac('sha256', keySecret)
        .update(`${razorpayOrderId}|${razorpayPaymentId}`)
        .digest('hex');

      if (generatedSignature !== razorpaySignature) {
        // Rollback reserved stock
        for (const item of order.items) {
          await Product.findByIdAndUpdate(item.productId, {
            $inc: { reservedStock: -item.quantity }
          });
        }
        order.paymentStatus = PAYMENT_STATUS.FAILED;
        order.orderStatus = ORDER_STATUS.FAILED;
        await order.save();
        throw new AppError('Payment signature verification failed.', 400, 'PAYMENT_VERIFICATION_FAILED');
      }
    }

    // Mark PAID, deduct stock, release reserved stock
    for (const item of order.items) {
      await Product.findByIdAndUpdate(item.productId, {
        $inc: { stock: -item.quantity, reservedStock: -item.quantity }
      });
    }

    order.paymentStatus = PAYMENT_STATUS.PAID;
    order.orderStatus = ORDER_STATUS.CONFIRMED;
    order.razorpayPaymentId = razorpayPaymentId;
    order.razorpaySignature = razorpaySignature;
    order.timeline[0].isCompleted = true;
    order.timeline[0].description = `Payment confirmed. Ref: ${razorpayPaymentId || 'ONLINE'}`;
    await order.save();

    // Clear cart
    const cart = await Cart.findOne({ userId: order.userId });
    if (cart) {
      cart.items = cart.items.filter(i => i.savedForLater);
      cart.appliedCoupon = null;
      await cart.save();
    }

    res.status(200).json({
      success: true,
      message: 'Payment verified successfully and order confirmed!',
      data: order
    });
  } catch (error) {
    next(error);
  }
};

export const getCustomerOrders = async (req, res, next) => {
  try {
    const { status, page = 1, limit = 20 } = req.query;
    const query = { userId: req.user._id };

    if (status && status !== 'all') {
      query.orderStatus = status;
    }

    const orders = await Order.find(query)
      .sort({ createdAt: -1 })
      .skip((page - 1) * limit)
      .limit(parseInt(limit, 10));

    res.status(200).json({
      success: true,
      data: orders
    });
  } catch (error) {
    next(error);
  }
};

export const getOrderById = async (req, res, next) => {
  try {
    const { id } = req.params;
    const order = await Order.findOne({
      _id: id,
      userId: req.user._id
    });

    if (!order) {
      throw new AppError('Order not found.', 404, 'ORDER_NOT_FOUND');
    }

    res.status(200).json({
      success: true,
      data: order
    });
  } catch (error) {
    next(error);
  }
};

export const cancelOrder = async (req, res, next) => {
  try {
    const { id } = req.params;
    const { reason = 'Cancelled by customer' } = req.body;

    const order = await Order.findOne({ _id: id, userId: req.user._id });
    if (!order) throw new AppError('Order not found.', 404, 'ORDER_NOT_FOUND');

    const cancellableStatuses = [ORDER_STATUS.PENDING_PAYMENT, ORDER_STATUS.CONFIRMED, ORDER_STATUS.PACKED];
    if (!cancellableStatuses.includes(order.orderStatus)) {
      throw new AppError(`Cannot cancel order in '${order.orderStatus}' state.`, 400, 'ORDER_NOT_CANCELLABLE');
    }

    // Restore inventory
    for (const item of order.items) {
      if (order.paymentStatus === PAYMENT_STATUS.PAID || order.paymentMethod === PAYMENT_METHOD.COD) {
        await Product.findByIdAndUpdate(item.productId, { $inc: { stock: item.quantity } });
      } else {
        await Product.findByIdAndUpdate(item.productId, { $inc: { reservedStock: -item.quantity } });
      }
    }

    order.orderStatus = ORDER_STATUS.CANCELLED;
    order.cancellationReason = reason;
    await order.save();

    res.status(200).json({
      success: true,
      message: 'Order cancelled successfully.',
      data: order
    });
  } catch (error) {
    next(error);
  }
};

export const trackOrder = async (req, res, next) => {
  try {
    const { id } = req.params;
    const order = await Order.findOne({ _id: id, userId: req.user._id });
    if (!order) throw new AppError('Order not found.', 404, 'ORDER_NOT_FOUND');

    res.status(200).json({
      success: true,
      data: {
        orderId: order._id.toString(),
        orderStatus: order.orderStatus,
        trackingNumber: order.trackingNumber || `TRK-${order.orderNumber.replace('HS-', '')}`,
        logisticsPartner: order.logisticsPartner,
        currentLat: order.currentLat,
        currentLng: order.currentLng,
        destLat: order.destLat,
        destLng: order.destLng,
        etaMinutes: order.etaMinutes
      }
    });
  } catch (error) {
    next(error);
  }
};
