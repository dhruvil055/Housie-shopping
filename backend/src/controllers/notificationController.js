import { Notification } from '../models/Notification.js';
import { AppError } from '../middleware/errorHandler.js';

/**
 * Get user's notifications (includes direct notifications and system broadcasts)
 * GET /api/v1/notifications
 */
export const getUserNotifications = async (req, res, next) => {
  try {
    const page = parseInt(req.query.page, 10) || 1;
    const limit = parseInt(req.query.limit, 10) || 20;

    const query = {
      $or: [
        { userId: req.user._id },
        { userId: null }
      ]
    };

    const total = await Notification.countDocuments(query);
    const unreadCount = await Notification.countDocuments({
      ...query,
      isRead: false
    });

    const notifications = await Notification.find(query)
      .sort({ createdAt: -1 })
      .skip((page - 1) * limit)
      .limit(limit)
      .lean();

    res.status(200).json({
      success: true,
      data: {
        notifications,
        unreadCount,
        pagination: {
          total,
          page,
          pages: Math.ceil(total / limit)
        }
      }
    });
  } catch (error) {
    next(error);
  }
};

/**
 * Mark a single notification as read
 * PATCH /api/v1/notifications/:id/read
 */
export const markNotificationAsRead = async (req, res, next) => {
  try {
    const notification = await Notification.findOneAndUpdate(
      {
        _id: req.params.id,
        $or: [{ userId: req.user._id }, { userId: null }]
      },
      {
        isRead: true,
        readAt: new Date()
      },
      { new: true }
    );

    if (!notification) {
      throw new AppError('Notification not found', 404, 'NOT_FOUND');
    }

    res.status(200).json({
      success: true,
      message: 'Notification marked as read',
      data: notification
    });
  } catch (error) {
    next(error);
  }
};

/**
 * Mark all notifications as read for current user
 * POST /api/v1/notifications/read-all
 */
export const markAllNotificationsAsRead = async (req, res, next) => {
  try {
    await Notification.updateMany(
      {
        $or: [{ userId: req.user._id }, { userId: null }],
        isRead: false
      },
      {
        isRead: true,
        readAt: new Date()
      }
    );

    res.status(200).json({
      success: true,
      message: 'All notifications marked as read'
    });
  } catch (error) {
    next(error);
  }
};

/**
 * Send notification (Admin / System broadcast or targeted user)
 * POST /api/v1/notifications/send
 */
export const sendNotification = async (req, res, next) => {
  try {
    const { userId, title, message, type, data } = req.body;
    if (!title || !message) {
      throw new AppError('Title and message are required', 400, 'INVALID_INPUT');
    }

    const notification = await Notification.create({
      userId: userId || null,
      title,
      message,
      type: type || 'SYSTEM',
      data: data || {}
    });

    res.status(201).json({
      success: true,
      message: 'Notification sent successfully',
      data: notification
    });
  } catch (error) {
    next(error);
  }
};
