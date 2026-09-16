import express from 'express';
import {
  getUserNotifications,
  markNotificationAsRead,
  markAllNotificationsAsRead,
  sendNotification
} from '../controllers/notificationController.js';
import { authenticate } from '../middleware/auth.js';
import { requireRole } from '../middleware/rbac.js';
import { ROLES } from '../config/constants.js';

const router = express.Router();

router.use(authenticate);

// Customer endpoints
router.get('/', getUserNotifications);
router.patch('/:id/read', markNotificationAsRead);
router.post('/read-all', markAllNotificationsAsRead);

// Admin send endpoint
router.post(
  '/send',
  requireRole(ROLES.STAFF, ROLES.MANAGER, ROLES.ADMIN, ROLES.SUPER_ADMIN),
  sendNotification
);

export default router;
