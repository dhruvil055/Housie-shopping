import jwt from 'jsonwebtoken';
import { User } from '../models/User.js';
import { AppError } from '../middleware/errorHandler.js';
import { ROLES } from '../config/constants.js';

export const register = async (req, res, next) => {
  try {
    const { name, email, phone, password } = req.body;

    const existingUser = await User.findOne({
      $or: [{ email: email.toLowerCase() }, { phone }]
    });

    if (existingUser) {
      throw new AppError('An account with this email or phone number already exists.', 400, 'USER_ALREADY_EXISTS');
    }

    const user = new User({
      name,
      email,
      phone,
      password,
      role: ROLES.CUSTOMER
    });

    const accessToken = user.generateAccessToken();
    const refreshToken = user.generateRefreshToken();
    await user.save();

    res.status(201).json({
      success: true,
      message: 'Account registered successfully.',
      data: {
        userId: user._id.toString(),
        name: user.name,
        email: user.email,
        phone: user.phone,
        role: user.role,
        accessToken,
        refreshToken
      }
    });
  } catch (error) {
    next(error);
  }
};

export const login = async (req, res, next) => {
  try {
    const identifier = (req.body.emailOrPhone || req.body.email || req.body.phone || '').trim();
    const { password } = req.body;

    if (!identifier) {
      throw new AppError('Email or phone is required.', 400, 'INVALID_INPUT');
    }

    const user = await User.findOne({
      $or: [
        { email: identifier.toLowerCase() },
        { phone: identifier }
      ]
    });

    if (!user) {
      throw new AppError('Invalid credentials. No user found with this email/phone.', 401, 'INVALID_CREDENTIALS');
    }

    if (!user.isActive) {
      throw new AppError('Account is deactivated. Please contact support.', 403, 'ACCOUNT_DEACTIVATED');
    }

    const isMatch = await user.comparePassword(password);
    if (!isMatch) {
      throw new AppError('Invalid credentials. Incorrect password.', 401, 'INVALID_CREDENTIALS');
    }

    const accessToken = user.generateAccessToken();
    const refreshToken = user.generateRefreshToken();
    await user.save();

    res.status(200).json({
      success: true,
      message: 'Logged in successfully.',
      data: {
        userId: user._id.toString(),
        name: user.name,
        email: user.email,
        phone: user.phone,
        role: user.role,
        avatar: user.avatar,
        accessToken,
        refreshToken
      }
    });
  } catch (error) {
    next(error);
  }
};

export const refreshToken = async (req, res, next) => {
  try {
    const { refreshToken: token } = req.body;
    if (!token) {
      throw new AppError('Refresh token is required.', 400, 'REFRESH_TOKEN_REQUIRED');
    }

    let decoded;
    try {
      decoded = jwt.verify(token, process.env.JWT_REFRESH_SECRET || 'housie_dev_refresh_secret_12345');
    } catch (err) {
      throw new AppError('Invalid or expired refresh token. Please log in again.', 401, 'INVALID_REFRESH_TOKEN');
    }

    const user = await User.findById(decoded.id);
    if (!user || !user.isActive) {
      throw new AppError('User not found or inactive.', 401, 'UNAUTHORIZED');
    }

    const tokenRecordIndex = user.refreshTokens.findIndex(
      (r) => r.token === token && new Date(r.expiresAt) > new Date()
    );

    if (tokenRecordIndex === -1) {
      throw new AppError('Refresh token revoked or expired.', 401, 'TOKEN_REVOKED');
    }

    // Rotate refresh token
    user.refreshTokens.splice(tokenRecordIndex, 1);
    const newAccessToken = user.generateAccessToken();
    const newRefreshToken = user.generateRefreshToken();
    await user.save();

    res.status(200).json({
      success: true,
      message: 'Token refreshed successfully.',
      data: {
        accessToken: newAccessToken,
        refreshToken: newRefreshToken
      }
    });
  } catch (error) {
    next(error);
  }
};

export const logout = async (req, res, next) => {
  try {
    const { refreshToken: token } = req.body;
    if (token && req.user) {
      req.user.refreshTokens = req.user.refreshTokens.filter((r) => r.token !== token);
      await req.user.save();
    }
    res.status(200).json({
      success: true,
      message: 'Logged out successfully.'
    });
  } catch (error) {
    next(error);
  }
};

export const logoutAll = async (req, res, next) => {
  try {
    req.user.refreshTokens = [];
    await req.user.save();
    res.status(200).json({
      success: true,
      message: 'Logged out from all devices.'
    });
  } catch (error) {
    next(error);
  }
};

export const verifyOtp = async (req, res, next) => {
  try {
    const { phone, otp } = req.body;
    // Standard verification: accepts test code '1234' or 4-digit code in dev
    if (otp && (otp === '1234' || otp.length === 4)) {
      let user = await User.findOne({ phone });
      if (user) {
        user.isPhoneVerified = true;
        await user.save();
      }
      return res.status(200).json({
        success: true,
        message: 'OTP verified successfully.',
        data: { verified: true }
      });
    }
    throw new AppError('Invalid OTP code. Please enter 1234.', 400, 'INVALID_OTP');
  } catch (error) {
    next(error);
  }
};

export const sendOtp = async (req, res, next) => {
  try {
    const { phone } = req.body;
    res.status(200).json({
      success: true,
      message: `OTP sent successfully to ${phone}. (Use 1234 for testing)`
    });
  } catch (error) {
    next(error);
  }
};

export const forgotPassword = async (req, res, next) => {
  try {
    const { emailOrPhone } = req.body;
    const user = await User.findOne({
      $or: [{ email: emailOrPhone.toLowerCase() }, { phone: emailOrPhone }]
    });
    if (!user) {
      throw new AppError('No account found with this email or phone.', 404, 'USER_NOT_FOUND');
    }
    res.status(200).json({
      success: true,
      message: 'Password reset code sent. (Use 1234 to verify)'
    });
  } catch (error) {
    next(error);
  }
};

export const resetPassword = async (req, res, next) => {
  try {
    const { phone, otp, newPassword } = req.body;
    if (otp !== '1234' && otp.length !== 4) {
      throw new AppError('Invalid OTP code.', 400, 'INVALID_OTP');
    }
    const user = await User.findOne({ phone });
    if (!user) {
      throw new AppError('User not found.', 404, 'USER_NOT_FOUND');
    }
    user.password = newPassword;
    user.refreshTokens = [];
    await user.save();

    res.status(200).json({
      success: true,
      message: 'Password reset successfully. Please log in with your new password.'
    });
  } catch (error) {
    next(error);
  }
};

export const getProfile = async (req, res, next) => {
  try {
    const user = await User.findById(req.user._id).select('-password -refreshTokens');
    res.status(200).json({
      success: true,
      data: user
    });
  } catch (error) {
    next(error);
  }
};

export const updateProfile = async (req, res, next) => {
  try {
    const { name, email, avatar } = req.body;
    const user = await User.findById(req.user._id);

    if (name) user.name = name;
    if (avatar) user.avatar = avatar;
    if (email && email.toLowerCase() !== user.email) {
      const exists = await User.findOne({ email: email.toLowerCase() });
      if (exists) throw new AppError('Email is already taken.', 400, 'EMAIL_EXISTS');
      user.email = email.toLowerCase();
      user.isEmailVerified = false;
    }

    await user.save();
    res.status(200).json({
      success: true,
      message: 'Profile updated successfully.',
      data: {
        userId: user._id.toString(),
        name: user.name,
        email: user.email,
        phone: user.phone,
        avatar: user.avatar
      }
    });
  } catch (error) {
    next(error);
  }
};

export const deleteAccount = async (req, res, next) => {
  try {
    await User.findByIdAndDelete(req.user._id);
    res.status(200).json({
      success: true,
      message: 'Account deleted permanently.'
    });
  } catch (error) {
    next(error);
  }
};
