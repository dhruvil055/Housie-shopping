import dotenv from 'dotenv';
import { createApp } from './app.js';
import { connectDB } from './config/db.js';
import { logger } from './config/logger.js';

dotenv.config();

const PORT = process.env.PORT || 5000;
const app = createApp();

const startServer = async () => {
  await connectDB();

  const server = app.listen(PORT, () => {
    logger.info(`=========================================`);
    logger.info(`Housie Shopping Production API Started`);
    logger.info(`Listening on port: ${PORT}`);
    logger.info(`Environment: ${process.env.NODE_ENV || 'development'}`);
    logger.info(`Health check: http://localhost:${PORT}/health`);
    logger.info(`=========================================`);
  });

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
