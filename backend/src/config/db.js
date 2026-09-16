import mongoose from 'mongoose';
import { logger } from './logger.js';

export const connectDB = async () => {
  const mongoUri = process.env.MONGO_URI || process.env.MONGODB_URI || 'mongodb://localhost:27017/housie_db';
  try {
    const conn = await mongoose.connect(mongoUri, {
      serverSelectionTimeoutMS: 5000,
      autoIndex: true
    });
    logger.info(`MongoDB Connected successfully: ${conn.connection.host}`);
    return conn;
  } catch (error) {
    logger.error(`MongoDB connection error: ${error.message}`);
    // Do not terminate process in development if Mongo is not running locally; allow mock/fallback or memory DB
    if (process.env.NODE_ENV === 'production') {
      process.exit(1);
    }
  }
};
