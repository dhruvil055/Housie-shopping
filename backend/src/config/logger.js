import winston from 'winston';

// Sensitive keys to mask from production logs
const SENSITIVE_KEYS = ['password', 'token', 'refreshToken', 'pin', 'otp', 'secret', 'authorization', 'signature'];

const redactSensitive = winston.format((info) => {
  const sanitize = (obj) => {
    if (!obj || typeof obj !== 'object') return obj;
    const clean = Array.isArray(obj) ? [] : {};
    for (const [key, value] of Object.entries(obj)) {
      if (SENSITIVE_KEYS.some(k => key.toLowerCase().includes(k))) {
        clean[key] = '[REDACTED]';
      } else if (typeof value === 'object' && value !== null) {
        clean[key] = sanitize(value);
      } else {
        clean[key] = value;
      }
    }
    return clean;
  };
  return sanitize(info);
});

export const logger = winston.createLogger({
  level: process.env.LOG_LEVEL || 'info',
  format: winston.format.combine(
    winston.format.timestamp(),
    redactSensitive(),
    winston.format.json()
  ),
  transports: [
    new winston.transports.Console({
      format: winston.format.combine(
        winston.format.colorize(),
        winston.format.printf(({ level, message, timestamp, ...meta }) => {
          return `${timestamp} [${level}]: ${message} ${Object.keys(meta).length ? JSON.stringify(meta) : ''}`;
        })
      )
    })
  ]
});
