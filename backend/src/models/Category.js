import mongoose from 'mongoose';

const subcategorySchema = new mongoose.Schema({
  name: { type: String, required: true },
  slug: { type: String, required: true },
  iconUrl: { type: String, default: '' },
  productCount: { type: Number, default: 0 }
}, { _id: true });

const categorySchema = new mongoose.Schema({
  name: { type: String, required: true, unique: true, trim: true },
  slug: { type: String, required: true, unique: true, lowercase: true, index: true },
  description: { type: String, default: '' },
  iconUrl: { type: String, default: '' },
  productCount: { type: Number, default: 0 },
  subcategories: [subcategorySchema],
  isActive: { type: Boolean, default: true, index: true },
  displayOrder: { type: Number, default: 0 }
}, {
  timestamps: true
});

export const Category = mongoose.model('Category', categorySchema);
