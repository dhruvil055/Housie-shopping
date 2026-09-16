import mongoose from 'mongoose';
import { ORDER_STATUS, PAYMENT_STATUS, PAYMENT_METHOD } from '../config/constants.js';

const orderItemSchema = new mongoose.Schema({
  productId: { type: mongoose.Schema.Types.ObjectId, ref: 'Product', required: true },
  title: { type: String, required: true },
  imageUrl: { type: String, default: '' },
  variantName: { type: String, default: '' },
  unitPrice: { type: Number, required: true },
  quantity: { type: Number, required: true, min: 1 },
  totalPrice: { type: Number, required: true }
}, { _id: false });

const orderTimelineSchema = new mongoose.Schema({
  status: { type: String, required: true },
  title: { type: String, required: true },
  description: { type: String, default: '' },
  timestamp: { type: String, default: () => new Date().toISOString() },
  isCompleted: { type: Boolean, default: false },
  isCurrent: { type: Boolean, default: false }
}, { _id: false });

const orderSchema = new mongoose.Schema({
  orderNumber: { type: String, required: true, unique: true, index: true },
  idempotencyKey: { type: String, unique: true, sparse: true, index: true },
  userId: { type: mongoose.Schema.Types.ObjectId, ref: 'User', required: true, index: true },
  customerName: { type: String, required: true },
  customerEmail: { type: String, required: true },
  customerPhone: { type: String, required: true },
  items: [orderItemSchema],
  shippingAddress: {
    fullName: String,
    phone: String,
    houseFlat: String,
    street: String,
    area: String,
    city: String,
    state: String,
    postalCode: String,
    landmark: String,
    lat: Number,
    lng: Number
  },
  paymentMethod: { type: String, default: PAYMENT_METHOD.RAZORPAY },
  paymentStatus: { type: String, enum: Object.values(PAYMENT_STATUS), default: PAYMENT_STATUS.PENDING, index: true },
  paymentId: { type: String, default: null },
  razorpayOrderId: { type: String, default: null, index: true },
  razorpayPaymentId: { type: String, default: null },
  orderStatus: { type: String, enum: Object.values(ORDER_STATUS), default: ORDER_STATUS.CONFIRMED, index: true },
  subtotal: { type: Number, required: true },
  discount: { type: Number, default: 0 },
  couponDiscount: { type: Number, default: 0 },
  couponCode: { type: String, default: null },
  tax: { type: Number, required: true },
  deliveryFee: { type: Number, default: 0 },
  totalAmount: { type: Number, required: true },
  estimatedDeliveryDate: { type: String, default: 'Within 2 business days' },
  trackingNumber: { type: String, default: '' },
  logisticsPartner: { type: String, default: 'Housie Express Fleet' },
  currentLat: { type: Number, default: 28.4595 },
  currentLng: { type: Number, default: 77.0266 },
  destLat: { type: Number, default: 28.4700 },
  destLng: { type: Number, default: 77.0300 },
  etaMinutes: { type: Number, default: 45 },
  cancellationReason: { type: String, default: null },
  timeline: [orderTimelineSchema]
}, {
  timestamps: true
});

export const Order = mongoose.model('Order', orderSchema);
