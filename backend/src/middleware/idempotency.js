const processedKeys = new Map();

// Clear old keys every 10 minutes
setInterval(() => {
  const now = Date.now();
  for (const [key, record] of processedKeys.entries()) {
    if (now - record.timestamp > 10 * 60 * 1000) {
      processedKeys.delete(key);
    }
  }
}, 10 * 60 * 1000);

export const checkIdempotency = (req, res, next) => {
  const idempotencyKey = req.headers['x-idempotency-key'];
  if (!idempotencyKey) {
    return next();
  }

  const existing = processedKeys.get(idempotencyKey);
  if (existing) {
    if (existing.status === 'PROCESSING') {
      return res.status(409).json({
        success: false,
        message: 'A request with this idempotency key is already being processed.',
        code: 'DUPLICATE_REQUEST_IN_PROGRESS'
      });
    }
    // Return cached completed response
    return res.status(existing.statusCode).json(existing.body);
  }

  // Mark key as processing
  processedKeys.set(idempotencyKey, {
    status: 'PROCESSING',
    timestamp: Date.now()
  });

  // Capture response
  const originalJson = res.json.bind(res);
  res.json = (body) => {
    processedKeys.set(idempotencyKey, {
      status: 'COMPLETED',
      statusCode: res.statusCode,
      body,
      timestamp: Date.now()
    });
    return originalJson(body);
  };

  next();
};
