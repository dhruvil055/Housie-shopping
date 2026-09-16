import mongoose from 'mongoose';

const cartItemSchema = new mongoose.Schema({
  productId: { type: mongoose.Schema.Types.ObjectId, ref: 'Product', required: true },
  variantId: { type: String, default: null },
  quantity: { type: Number, required: true, min: 1, default: 1 },
  savedForLater: { type: Boolean, default: false }
}, { _id: true });

const cartSchema = new mongoose.Schema({
  userId: { type: mongoose.Schema.Types.ObjectId, ref: 'User', required: true, unique: true, index: true },
  items: [cartItemSchema],
  appliedCoupon: { type: String, default: null }
}, {
  timestamps: true
});

export const Cart = mongoose.model('Cart', cartSchema);
