import dotenv from 'dotenv';
import mongoose from 'mongoose';
import { User } from '../models/User.js';
import { Category } from '../models/Category.js';
import { Product } from '../models/Product.js';
import { Banner } from '../models/Banner.js';
import { Coupon } from '../models/Coupon.js';
import { AuditLog } from '../models/AuditLog.js';
import { seedUsers, seedCategories, seedProducts, seedBanners, seedCoupons } from './seedData.js';
import { logger } from '../config/logger.js';

dotenv.config();

const seedDatabase = async () => {
  const mongoUri = process.env.MONGO_URI || process.env.MONGODB_URI || 'mongodb://localhost:27017/housie_db';
  try {
    await mongoose.connect(mongoUri);
    logger.info('Connected to MongoDB for database seeding.');

    // Clear existing collections
    await Promise.all([
      User.deleteMany(),
      Category.deleteMany(),
      Product.deleteMany(),
      Banner.deleteMany(),
      Coupon.deleteMany(),
      AuditLog.deleteMany()
    ]);
    logger.info('Cleared existing collections.');

    // Seed Users (saving individually to trigger password hashing)
    for (const u of seedUsers) {
      const user = new User(u);
      await user.save();
    }
    logger.info(`Seeded ${seedUsers.length} users (Admin & Customer).`);

    // Seed Categories
    await Category.insertMany(seedCategories);
    logger.info(`Seeded ${seedCategories.length} product categories.`);

    // Seed Products
    await Product.insertMany(seedProducts);
    logger.info(`Seeded ${seedProducts.length} commercial products.`);

    // Seed Banners
    await Banner.insertMany(seedBanners);
    logger.info(`Seeded ${seedBanners.length} promotional banners.`);

    // Seed Coupons
    await Coupon.insertMany(seedCoupons);
    logger.info(`Seeded ${seedCoupons.length} discount coupons.`);

    // Initial audit log
    await AuditLog.create({
      adminUser: 'System Seeder',
      role: 'SUPER_ADMIN',
      action: 'SYSTEM_INITIALIZED',
      details: 'Database seeded with complete home building material catalog and initial accounts.'
    });

    logger.info('Database seeding completed successfully!');
    process.exit(0);
  } catch (error) {
    logger.error(`Error during database seeding: ${error.message}`);
    process.exit(1);
  }
};

seedDatabase();
