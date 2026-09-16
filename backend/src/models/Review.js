import mongoose from 'mongoose';

const reviewSchema = new mongoose.Schema({
  productId: { type: mongoose.Schema.Types.ObjectId, ref: 'Product', required: true, index: true },
  userId: { type: mongoose.Schema.Types.ObjectId, ref: 'User', required: true },
  userName: { type: String, required: true },
  rating: { type: Number, required: true, min: 1, max: 5 },
  title: { type: String, default: '' },
  comment: { type: String, required: true },
  isVerifiedPurchase: { type: Boolean, default: true },
  isApproved: { type: Boolean, default: true, index: true }
}, {
  timestamps: true
});

export const Review = mongoose.model('Review', reviewSchema);
