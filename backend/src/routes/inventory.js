import express from 'express';
import {
  checkStock,
  reserveStock,
  releaseStock,
  getLowStockItems
} from '../controllers/inventoryController.js';
import { authenticate } from '../middleware/auth.js';
import { requireRole } from '../middleware/rbac.js';
import { ROLES } from '../config/constants.js';

const router = express.Router();

// Public / Customer: check stock availability
router.post('/check', checkStock);

// Authenticated customer/system: reserve and release stock
router.post('/reserve', authenticate, reserveStock);
router.post('/release', authenticate, releaseStock);

// Staff / Manager / Admin: low stock monitoring
router.get(
  '/low-stock',
  authenticate,
  requireRole(ROLES.STAFF, ROLES.MANAGER, ROLES.ADMIN, ROLES.SUPER_ADMIN),
  getLowStockItems
);

export default router;
