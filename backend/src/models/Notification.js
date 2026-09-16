import mongoose from 'mongoose';

const notificationSchema = new mongoose.Schema({
  userId: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User',
    required: false,
    index: true
  },
  title: {
    type: String,
    required: true,
    trim: true
  },
  message: {
    type: String,
    required: true,
    trim: true
  },
  type: {
    type: String,
    enum: ['ORDER_STATUS', 'PROMOTION', 'PRICE_DROP', 'ACCOUNT', 'SYSTEM', 'PAYMENT'],
    default: 'SYSTEM',
    index: true
  },
  data: {
    orderId: { type: String },
    orderNumber: { type: String },
    productId: { type: String },
    deepLink: { type: String }
  },
  isRead: {
    type: Boolean,
    default: false,
    index: true
  },
  readAt: {
    type: Date
  }
}, {
  timestamps: true
});

notificationSchema.index({ userId: 1, createdAt: -1 });

export const Notification = mongoose.model('Notification', notificationSchema);
