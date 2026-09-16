import mongoose from 'mongoose';
import { logger } from './logger.js';

const DEFAULT_ATLAS_URI = 'mongodb+srv://dhruvilkyada483_db_user:LYk3gJyFMSFBJ0a2@housingshoppingcluster.lxws1me.mongodb.net/housie_production?retryWrites=true&w=majority&appName=HousingShoppingCluster';

export const connectDB = async () => {
  const mongoUri = process.env.MONGO_URI || process.env.MONGODB_URI || DEFAULT_ATLAS_URI;
  try {
    const conn = await mongoose.connect(mongoUri, {
      serverSelectionTimeoutMS: 10000,
      autoIndex: true
    });
    logger.info(`MongoDB Connected successfully: ${conn.connection.host}`);
    return conn;
  } catch (error) {
    logger.error(`MongoDB connection error: ${error.message}`);
    // Non-blocking retry after delay to prevent container termination
    setTimeout(connectDB, 5000);
  }
};
