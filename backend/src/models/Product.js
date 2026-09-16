import mongoose from 'mongoose';

const variantSchema = new mongoose.Schema({
  name: { type: String, required: true },
  sku: { type: String, default: '' },
  price: { type: Number, required: true },
  mrp: { type: Number, required: true },
  stock: { type: Number, required: true, default: 0 },
  grade: { type: String, default: '' },
  unit: { type: String, default: '' },
  size: { type: String, default: '' },
  color: { type: String, default: '' }
}, { _id: true });

const specificationSchema = new mongoose.Schema({
  key: { type: String, required: true },
  value: { type: String, required: true }
}, { _id: false });

const sellerSchema = new mongoose.Schema({
  name: { type: String, default: 'Housie Direct Verified' },
  storeName: { type: String, default: 'Housie Official Depot' },
  rating: { type: Number, default: 4.8 }
}, { _id: false });

const productSchema = new mongoose.Schema({
  sku: { type: String, required: true, unique: true, uppercase: true, trim: true, index: true },
  title: { type: String, required: true, trim: true },
  description: { type: String, required: true },
  brand: { type: String, required: true, trim: true, index: true },
  categoryId: { type: String, default: 'cat-general', index: true },
  categoryName: { type: String, required: true, index: true },
  price: { type: Number, required: true, min: 0 },
  mrp: { type: Number, required: true, min: 0 },
  stock: { type: Number, required: true, min: 0, default: 0 },
  reservedStock: { type: Number, default: 0, min: 0 },
  images: [{ type: String }],
  deliveryEstimateDays: { type: Number, default: 2 },
  isFeatured: { type: Boolean, default: false, index: true },
  isBestSeller: { type: Boolean, default: false, index: true },
  isDealOfDay: { type: Boolean, default: false, index: true },
  isActive: { type: Boolean, default: true, index: true },
  seller: { type: sellerSchema, default: () => ({}) },
  variants: [variantSchema],
  specifications: [specificationSchema],
  tags: [{ type: String, index: true }],
  rating: { type: Number, default: 4.5, min: 0, max: 5 },
  reviewCount: { type: Number, default: 0 }
}, {
  timestamps: true,
  toJSON: { virtuals: true },
  toObject: { virtuals: true }
});

// Text index for multi-field search
productSchema.index({ title: 'text', description: 'text', brand: 'text', categoryName: 'text', tags: 'text' });

// Virtuals
productSchema.virtual('availableStock').get(function () {
  return Math.max(0, this.stock - (this.reservedStock || 0));
});

productSchema.virtual('discountPercentage').get(function () {
  if (this.mrp > this.price) {
    return Math.round(((this.mrp - this.price) / this.mrp) * 100);
  }
  return 0;
});

export const Product = mongoose.model('Product', productSchema);
