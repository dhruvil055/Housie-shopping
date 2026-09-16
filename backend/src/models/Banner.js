import mongoose from 'mongoose';

const bannerSchema = new mongoose.Schema({
  title: { type: String, required: true },
  subtitle: { type: String, default: '' },
  imageUrl: { type: String, required: true },
  targetType: { type: String, enum: ['CATEGORY', 'PRODUCT', 'EXTERNAL'], default: 'CATEGORY' },
  targetId: { type: String, default: '' },
  categoryTarget: { type: String, default: '' },
  displayOrder: { type: Number, default: 0 },
  isActive: { type: Boolean, default: true, index: true }
}, {
  timestamps: true
});

export const Banner = mongoose.model('Banner', bannerSchema);
