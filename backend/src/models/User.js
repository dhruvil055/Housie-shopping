import mongoose from 'mongoose';
import bcrypt from 'bcryptjs';
import jwt from 'jsonwebtoken';
import { ROLES } from '../config/constants.js';

const addressSchema = new mongoose.Schema({
  fullName: { type: String, required: true },
  phone: { type: String, required: true },
  houseFlat: { type: String, required: true },
  street: { type: String, default: '' },
  area: { type: String, default: '' },
  city: { type: String, required: true },
  state: { type: String, required: true },
  postalCode: { type: String, required: true },
  country: { type: String, default: 'India' },
  landmark: { type: String, default: '' },
  lat: { type: Number, default: 0.0 },
  lng: { type: Number, default: 0.0 },
  addressType: { type: String, enum: ['Home', 'Work', 'Other'], default: 'Home' },
  isDefault: { type: Boolean, default: false }
}, { _id: true, timestamps: true });

const userSchema = new mongoose.Schema({
  name: { type: String, required: true, trim: true },
  email: { type: String, required: true, unique: true, lowercase: true, trim: true, index: true },
  phone: { type: String, required: true, unique: true, trim: true, index: true },
  password: { type: String, required: true },
  role: {
    type: String,
    enum: Object.values(ROLES),
    default: ROLES.CUSTOMER,
    index: true
  },
  avatar: { type: String, default: 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=500' },
  isEmailVerified: { type: Boolean, default: false },
  isPhoneVerified: { type: Boolean, default: false },
  isActive: { type: Boolean, default: true },
  addresses: [addressSchema],
  refreshTokens: [{
    token: { type: String, required: true },
    expiresAt: { type: Date, required: true }
  }]
}, {
  timestamps: true
});

// Hash password before saving
userSchema.pre('save', async function (next) {
  if (!this.isModified('password')) return next();
  const salt = await bcrypt.genSalt(10);
  this.password = await bcrypt.hash(this.password, salt);
  next();
});

// Compare password
userSchema.methods.comparePassword = async function (candidatePassword) {
  return bcrypt.compare(candidatePassword, this.password);
};

// Generate short-lived Access Token (15m)
userSchema.methods.generateAccessToken = function () {
  return jwt.sign(
    {
      id: this._id.toString(),
      email: this.email,
      role: this.role,
      name: this.name
    },
    process.env.JWT_SECRET || 'housie_dev_access_secret_12345',
    { expiresIn: '15m' }
  );
};

// Generate Refresh Token (7d)
userSchema.methods.generateRefreshToken = function () {
  const token = jwt.sign(
    { id: this._id.toString(), jti: Math.random().toString(36).substring(2) + Date.now().toString(36) },
    process.env.JWT_REFRESH_SECRET || 'housie_dev_refresh_secret_12345',
    { expiresIn: '7d' }
  );
  const expiresAt = new Date(Date.now() + 7 * 24 * 60 * 60 * 1000);
  this.refreshTokens.push({ token, expiresAt });
  return token;
};

export const User = mongoose.model('User', userSchema);
