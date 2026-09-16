import mongoose from 'mongoose';

const couponSchema = new mongoose.Schema({
  code: { type: String, required: true, unique: true, uppercase: true, trim: true, index: true },
  description: { type: String, default: '' },
  discountType: { type: String, enum: ['PERCENTAGE', 'FIXED_AMOUNT'], default: 'PERCENTAGE' },
  discountValue: { type: Number, required: true, min: 0 },
  minOrderAmount: { type: Number, default: 0, min: 0 },
  maxDiscountAmount: { type: Number, default: null },
  validUntil: { type: Date, required: true },
  maxUses: { type: Number, default: 1000 },
  usedCount: { type: Number, default: 0 },
  perUserLimit: { type: Number, default: 1 },
  isActive: { type: Boolean, default: true, index: true }
}, {
  timestamps: true
});

couponSchema.methods.isValid = function (orderAmount) {
  if (!this.isActive) return false;
  if (new Date() > this.validUntil) return false;
  if (this.usedCount >= this.maxUses) return false;
  if (orderAmount < this.minOrderAmount) return false;
  return true;
};

export const Coupon = mongoose.model('Coupon', couponSchema);
