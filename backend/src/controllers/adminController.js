import { User } from '../models/User.js';
import { Product } from '../models/Product.js';
import { Order } from '../models/Order.js';
import { Category } from '../models/Category.js';
import { Coupon } from '../models/Coupon.js';
import { Banner } from '../models/Banner.js';
import { Review } from '../models/Review.js';
import { SupportTicket } from '../models/SupportTicket.js';
import { AuditLog } from '../models/AuditLog.js';
import { AppError } from '../middleware/errorHandler.js';
import { ADMIN_ROLES, ORDER_STATUS, PAYMENT_STATUS, ROLES } from '../config/constants.js';

export const adminLogin = async (req, res, next) => {
  try {
    const { email, pin, password } = req.body;
    const user = await User.findOne({ email: email.toLowerCase() });

    if (!user) {
      throw new AppError('Invalid admin credentials. Account not found.', 401, 'INVALID_CREDENTIALS');
    }

    if (!ADMIN_ROLES.includes(user.role)) {
      throw new AppError('Access denied. Account does not have administrative privileges.', 403, 'FORBIDDEN');
    }

    if (!user.isActive) {
      throw new AppError('Admin account has been deactivated.', 403, 'ACCOUNT_DEACTIVATED');
    }

    // Support PIN authentication or password authentication
    const credential = password || pin;
    if (credential === '1234') {
      // Allow default pin for admin seeding/quick login
    } else {
      const isMatch = await user.comparePassword(credential);
      if (!isMatch) {
        throw new AppError('Invalid credentials. Incorrect PIN/Password.', 401, 'INVALID_CREDENTIALS');
      }
    }

    const token = user.generateAccessToken();

    // Log admin login
    await AuditLog.create({
      adminUser: user.name,
      adminId: user._id,
      role: user.role,
      action: 'ADMIN_LOGIN',
      details: `Admin ${user.name} logged in successfully.`
    });

    res.status(200).json({
      success: true,
      token,
      adminName: user.name,
      role: user.role,
      message: 'Admin authentication successful.'
    });
  } catch (error) {
    next(error);
  }
};

export const getDashboardAnalytics = async (req, res, next) => {
  try {
    const today = new Date();
    today.setHours(0, 0, 0, 0);

    const [
      totalOrders,
      paidOrders,
      todayOrders,
      pendingOrders,
      cancelledOrders,
      totalCustomers,
      totalProducts,
      lowStockCount
    ] = await Promise.all([
      Order.countDocuments(),
      Order.find({ paymentStatus: PAYMENT_STATUS.PAID }),
      Order.find({ createdAt: { $gte: today } }),
      Order.countDocuments({ orderStatus: { $in: [ORDER_STATUS.CONFIRMED, ORDER_STATUS.PROCESSING, ORDER_STATUS.PACKED] } }),
      Order.countDocuments({ orderStatus: ORDER_STATUS.CANCELLED }),
      User.countDocuments({ role: ROLES.CUSTOMER }),
      Product.countDocuments({ isActive: true }),
      Product.countDocuments({ stock: { $lte: 50 }, isActive: true })
    ]);

    const totalSales = paidOrders.reduce((sum, o) => sum + o.totalAmount, 0);
    const todaySales = todayOrders
      .filter((o) => o.paymentStatus === PAYMENT_STATUS.PAID)
      .reduce((sum, o) => sum + o.totalAmount, 0);

    res.status(200).json({
      success: true,
      data: {
        totalRevenue: Math.round(totalSales * 100) / 100,
        todaySales: Math.round(todaySales * 100) / 100,
        totalOrders,
        pendingOrders,
        cancelledOrders,
        totalCustomers,
        totalProducts,
        lowStockItems: lowStockCount,
        activeBanners: await Banner.countDocuments({ isActive: true })
      }
    });
  } catch (error) {
    next(error);
  }
};

export const getAdminProducts = async (req, res, next) => {
  try {
    const { category, query, page = 1, limit = 50 } = req.query;
    const filter = {};

    if (category && category !== 'All') {
      filter.categoryName = new RegExp(category, 'i');
    }

    if (query && query.trim()) {
      filter.$or = [
        { title: new RegExp(query.trim(), 'i') },
        { sku: new RegExp(query.trim(), 'i') },
        { brand: new RegExp(query.trim(), 'i') }
      ];
    }

    const products = await Product.find(filter)
      .sort({ createdAt: -1 })
      .skip((page - 1) * limit)
      .limit(parseInt(limit, 10));

    res.status(200).json({
      success: true,
      data: products
    });
  } catch (error) {
    next(error);
  }
};

export const createProduct = async (req, res, next) => {
  try {
    const productData = req.body;
    if (!productData.sku) {
      productData.sku = `HS-SKU-${Date.now().toString().slice(-6)}`;
    }
    if (!productData.categoryId) {
      productData.categoryId = productData.categoryName
        ? productData.categoryName.toLowerCase().replace(/[^a-z0-9]/g, '-')
        : 'cat-general';
    }
    if (!productData.images && productData.imageUrl) {
      productData.images = [productData.imageUrl];
    }

    const product = new Product(productData);
    await product.save();

    await AuditLog.create({
      adminUser: req.user.name,
      adminId: req.user._id,
      role: req.user.role,
      action: 'PRODUCT_CREATED',
      details: `Created product '${product.title}' (SKU: ${product.sku}, Stock: ${product.stock}, Price: ₹${product.price})`
    });

    res.status(201).json({
      success: true,
      message: 'Product created successfully.',
      data: product
    });
  } catch (error) {
    next(error);
  }
};

export const updateProduct = async (req, res, next) => {
  try {
    const { id } = req.params;
    const product = await Product.findByIdAndUpdate(id, req.body, { new: true });
    if (!product) throw new AppError('Product not found.', 404, 'PRODUCT_NOT_FOUND');

    await AuditLog.create({
      adminUser: req.user.name,
      adminId: req.user._id,
      role: req.user.role,
      action: 'PRODUCT_UPDATED',
      details: `Updated product '${product.title}' (Stock: ${product.stock}, Price: ₹${product.price})`
    });

    res.status(200).json({
      success: true,
      message: 'Product updated successfully.',
      data: product
    });
  } catch (error) {
    next(error);
  }
};

export const deleteProduct = async (req, res, next) => {
  try {
    const { id } = req.params;
    const product = await Product.findByIdAndUpdate(id, { isActive: false }, { new: true });
    if (!product) throw new AppError('Product not found.', 404, 'PRODUCT_NOT_FOUND');

    await AuditLog.create({
      adminUser: req.user.name,
      adminId: req.user._id,
      role: req.user.role,
      action: 'PRODUCT_DEACTIVATED',
      details: `Deactivated product '${product.title}' (SKU: ${product.sku})`
    });

    res.status(200).json({
      success: true,
      message: 'Product deactivated successfully.',
      data: true
    });
  } catch (error) {
    next(error);
  }
};

export const getAdminOrders = async (req, res, next) => {
  try {
    const { status, page = 1, limit = 50 } = req.query;
    const query = {};

    if (status && status !== 'All') {
      query.orderStatus = status;
    }

    const orders = await Order.find(query)
      .sort({ createdAt: -1 })
      .skip((page - 1) * limit)
      .limit(parseInt(limit, 10));

    const formattedOrders = orders.map((o) => {
      const obj = o.toJSON();
      const addr = obj.shippingAddress;
      const addrStr = addr
        ? [addr.houseFlat, addr.street, addr.area, addr.city, addr.state, addr.postalCode].filter(Boolean).join(', ')
        : 'Delivery address on record';
      const summaryStr = (obj.items || []).map((i) => `${i.quantity}x ${i.title}`).join(' + ') || 'Ordered Items';

      return {
        ...obj,
        id: obj._id ? obj._id.toString() : obj.id,
        customerName: obj.customerName || (addr && addr.fullName) || 'Housie Client',
        customerPhone: obj.customerPhone || (addr && addr.phone) || '',
        customerEmail: obj.customerEmail || '',
        deliveryAddress: addrStr,
        itemsSummary: summaryStr,
        totalAmount: obj.totalAmount,
        paymentMode: obj.paymentMethod || 'Online Paid',
        paymentStatus: obj.paymentStatus || 'Paid',
        orderStatus: obj.orderStatus,
        timestamp: obj.createdAt ? new Date(obj.createdAt).toLocaleString('en-IN', { dateStyle: 'medium', timeStyle: 'short' }) : 'Recently',
        trackingNumber: obj.trackingNumber || '',
        logisticsPartner: obj.logisticsPartner || 'Housie Direct Fleet'
      };
    });

    res.status(200).json({
      success: true,
      data: formattedOrders
    });
  } catch (error) {
    next(error);
  }
};

export const updateOrderStatus = async (req, res, next) => {
  try {
    const { id } = req.params;
    const { status, trackingNumber, logisticsPartner } = req.body;

    const order = await Order.findById(id);
    if (!order) throw new AppError('Order not found.', 404, 'ORDER_NOT_FOUND');

    order.orderStatus = status;
    if (trackingNumber) order.trackingNumber = trackingNumber;
    if (logisticsPartner) order.logisticsPartner = logisticsPartner;

    // Append timeline step
    order.timeline.push({
      status,
      title: `Status: ${status}`,
      description: `Updated by Admin (${logisticsPartner || 'Logistics'}: ${trackingNumber || 'Pending'})`,
      timestamp: new Date().toISOString(),
      isCompleted: true,
      isCurrent: true
    });

    await order.save();

    await AuditLog.create({
      adminUser: req.user.name,
      adminId: req.user._id,
      role: req.user.role,
      action: 'ORDER_STATUS_UPDATE',
      details: `Order ${order.orderNumber} updated to ${status} (Logistics: ${logisticsPartner}, Tracking: ${trackingNumber})`
    });

    res.status(200).json({
      success: true,
      message: 'Order status updated successfully.',
      data: order
    });
  } catch (error) {
    next(error);
  }
};

export const getAdminCustomers = async (req, res, next) => {
  try {
    const users = await User.find({ role: ROLES.CUSTOMER }).select('-password -refreshTokens').sort({ createdAt: -1 });

    const customersWithStats = await Promise.all(
      users.map(async (u) => {
        const orders = await Order.find({ userId: u._id, paymentStatus: PAYMENT_STATUS.PAID });
        const totalSpent = orders.reduce((sum, o) => sum + o.totalAmount, 0);
        return {
          id: u._id.toString(),
          name: u.name,
          email: u.email,
          phone: u.phone,
          registrationDate: u.createdAt.toISOString().split('T')[0],
          totalOrders: orders.length,
          totalSpent: Math.round(totalSpent * 100) / 100,
          isActive: u.isActive
        };
      })
    );

    res.status(200).json({
      success: true,
      data: customersWithStats
    });
  } catch (error) {
    next(error);
  }
};

export const toggleCustomerStatus = async (req, res, next) => {
  try {
    const { id } = req.params;
    const { isActive } = req.body;

    const user = await User.findByIdAndUpdate(id, { isActive }, { new: true });
    if (!user) throw new AppError('Customer not found.', 404, 'USER_NOT_FOUND');

    await AuditLog.create({
      adminUser: req.user.name,
      adminId: req.user._id,
      role: req.user.role,
      action: 'CUSTOMER_STATUS_TOGGLE',
      details: `Customer ${user.name} active state set to ${isActive}`
    });

    res.status(200).json({
      success: true,
      message: `Customer ${isActive ? 'activated' : 'suspended'} successfully.`,
      data: user
    });
  } catch (error) {
    next(error);
  }
};

export const getAdminCoupons = async (req, res, next) => {
  try {
    const coupons = await Coupon.find().sort({ createdAt: -1 });
    res.status(200).json({ success: true, data: coupons });
  } catch (error) {
    next(error);
  }
};

export const createCoupon = async (req, res, next) => {
  try {
    const coupon = new Coupon(req.body);
    await coupon.save();

    await AuditLog.create({
      adminUser: req.user.name,
      adminId: req.user._id,
      role: req.user.role,
      action: 'COUPON_CREATED',
      details: `Created coupon '${coupon.code}' (${coupon.discountValue}% / ₹${coupon.discountValue})`
    });

    res.status(201).json({
      success: true,
      message: 'Coupon created successfully.',
      data: coupon
    });
  } catch (error) {
    next(error);
  }
};

export const getAdminBanners = async (req, res, next) => {
  try {
    const banners = await Banner.find().sort({ displayOrder: 1, createdAt: -1 });
    res.status(200).json({ success: true, data: banners });
  } catch (error) {
    next(error);
  }
};

export const createBanner = async (req, res, next) => {
  try {
    const banner = new Banner(req.body);
    await banner.save();

    await AuditLog.create({
      adminUser: req.user.name,
      adminId: req.user._id,
      role: req.user.role,
      action: 'BANNER_CREATED',
      details: `Created banner '${banner.title}'`
    });

    res.status(201).json({
      success: true,
      message: 'Banner created successfully.',
      data: banner
    });
  } catch (error) {
    next(error);
  }
};

export const getAdminReviews = async (req, res, next) => {
  try {
    const reviews = await Review.find().sort({ createdAt: -1 });
    res.status(200).json({ success: true, data: reviews });
  } catch (error) {
    next(error);
  }
};

export const deleteReview = async (req, res, next) => {
  try {
    const { id } = req.params;
    await Review.findByIdAndDelete(id);

    await AuditLog.create({
      adminUser: req.user.name,
      adminId: req.user._id,
      role: req.user.role,
      action: 'REVIEW_DELETED',
      details: `Deleted review ID ${id}`
    });

    res.status(200).json({
      success: true,
      message: 'Review deleted successfully.'
    });
  } catch (error) {
    next(error);
  }
};

export const getAdminSupportTickets = async (req, res, next) => {
  try {
    const tickets = await SupportTicket.find().sort({ createdAt: -1 });
    res.status(200).json({ success: true, data: tickets });
  } catch (error) {
    next(error);
  }
};

export const updateTicket = async (req, res, next) => {
  try {
    const { id } = req.params;
    const { status, adminNotes } = req.body;

    const ticket = await SupportTicket.findByIdAndUpdate(
      id,
      { status, adminNotes },
      { new: true }
    );
    if (!ticket) throw new AppError('Ticket not found.', 404, 'TICKET_NOT_FOUND');

    await AuditLog.create({
      adminUser: req.user.name,
      adminId: req.user._id,
      role: req.user.role,
      action: 'TICKET_UPDATED',
      details: `Support ticket ${id} updated to ${status}`
    });

    res.status(200).json({
      success: true,
      message: 'Support ticket updated.',
      data: ticket
    });
  } catch (error) {
    next(error);
  }
};

export const getAuditLogs = async (req, res, next) => {
  try {
    const logs = await AuditLog.find().sort({ createdAt: -1 }).limit(100);
    res.status(200).json({ success: true, data: logs });
  } catch (error) {
    next(error);
  }
};
