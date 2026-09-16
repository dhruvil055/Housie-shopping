import dotenv from 'dotenv';
import { createApp } from './app.js';
import { connectDB } from './config/db.js';
import { logger } from './config/logger.js';

dotenv.config();

const PORT = process.env.PORT || 5000;
const app = createApp();

const startServer = async () => {
  // Bind immediately to 0.0.0.0 so cloud providers (Render, AWS, GCP) detect open port immediately
  const server = app.listen(PORT, '0.0.0.0', () => {
    logger.info(`=========================================`);
    logger.info(`Housie Shopping Production API Started`);
    logger.info(`Listening on port: ${PORT} (0.0.0.0)`);
    logger.info(`Environment: ${process.env.NODE_ENV || 'development'}`);
    logger.info(`Health check: http://0.0.0.0:${PORT}/health`);
    logger.info(`=========================================`);
  });

  // Connect to MongoDB Atlas
  try {
    await connectDB();
  } catch (err) {
    logger.error(`Initial database connection error: ${err.message}`);
  }

  const shutdown = () => {
    logger.info('Shutting down server gracefully...');
    server.close(() => {
      logger.info('HTTP server closed.');
      process.exit(0);
    });
  };

  process.on('SIGINT', shutdown);
  process.on('SIGTERM', shutdown);
};

startServer();
