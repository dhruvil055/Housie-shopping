import express from 'express';
import {
  adminLogin,
  getDashboardAnalytics,
  getAdminProducts,
  createProduct,
  updateProduct,
  deleteProduct,
  getAdminOrders,
  updateOrderStatus,
  getAdminCustomers,
  toggleCustomerStatus,
  getAdminCoupons,
  createCoupon,
  getAdminBanners,
  createBanner,
  getAdminReviews,
  deleteReview,
  getAdminSupportTickets,
  updateTicket,
  getAuditLogs
} from '../controllers/adminController.js';
import { authenticate } from '../middleware/auth.js';
import { requireRole } from '../middleware/rbac.js';
import { ADMIN_ROLES, ROLES } from '../config/constants.js';

const router = express.Router();

// Admin Login (Public for administrative credentials)
router.post('/login', adminLogin);

// Protected Admin Routes (Requires token and staff/manager/admin role)
router.use(authenticate);
router.use(requireRole(...ADMIN_ROLES));

// Analytics
router.get('/analytics', getDashboardAnalytics);

// Products & Inventory
router.get('/products', getAdminProducts);
router.post('/products', requireRole(ROLES.MANAGER, ROLES.ADMIN, ROLES.SUPER_ADMIN), createProduct);
router.put('/products/:id', requireRole(ROLES.MANAGER, ROLES.ADMIN, ROLES.SUPER_ADMIN), updateProduct);
router.delete('/products/:id', requireRole(ROLES.ADMIN, ROLES.SUPER_ADMIN), deleteProduct);

// Orders
router.get('/orders', getAdminOrders);
router.patch('/orders/:id/status', updateOrderStatus);

// Customers
router.get('/customers', getAdminCustomers);
router.patch('/customers/:id/status', requireRole(ROLES.ADMIN, ROLES.SUPER_ADMIN), toggleCustomerStatus);

// Coupons
router.get('/coupons', getAdminCoupons);
router.post('/coupons', requireRole(ROLES.MANAGER, ROLES.ADMIN, ROLES.SUPER_ADMIN), createCoupon);

// Banners
router.get('/banners', getAdminBanners);
router.post('/banners', requireRole(ROLES.MANAGER, ROLES.ADMIN, ROLES.SUPER_ADMIN), createBanner);

// Reviews & Support
router.get('/reviews', getAdminReviews);
router.delete('/reviews/:id', requireRole(ROLES.ADMIN, ROLES.SUPER_ADMIN), deleteReview);
router.get('/support/tickets', getAdminSupportTickets);
router.patch('/support/tickets/:id', updateTicket);

// Audit Logs (Super Admin & Admin only)
router.get('/audit-logs', requireRole(ROLES.ADMIN, ROLES.SUPER_ADMIN), getAuditLogs);

export default router;
