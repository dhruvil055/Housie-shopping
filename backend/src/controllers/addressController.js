import { User } from '../models/User.js';
import { AppError } from '../middleware/errorHandler.js';

export const getAddresses = async (req, res, next) => {
  try {
    const user = await User.findById(req.user._id).select('addresses');
    res.status(200).json({
      success: true,
      data: user.addresses || []
    });
  } catch (error) {
    next(error);
  }
};

export const addAddress = async (req, res, next) => {
  try {
    const user = await User.findById(req.user._id);
    const newAddress = req.body;

    if (newAddress.isDefault || user.addresses.length === 0) {
      user.addresses.forEach((a) => (a.isDefault = false));
      newAddress.isDefault = true;
    }

    user.addresses.push(newAddress);
    await user.save();

    res.status(201).json({
      success: true,
      message: 'Address added successfully.',
      data: user.addresses[user.addresses.length - 1]
    });
  } catch (error) {
    next(error);
  }
};

export const updateAddress = async (req, res, next) => {
  try {
    const { id } = req.params;
    const user = await User.findById(req.user._id);
    const address = user.addresses.id(id);

    if (!address) {
      throw new AppError('Address not found.', 404, 'ADDRESS_NOT_FOUND');
    }

    if (req.body.isDefault) {
      user.addresses.forEach((a) => (a.isDefault = false));
    }

    Object.assign(address, req.body);
    await user.save();

    res.status(200).json({
      success: true,
      message: 'Address updated successfully.',
      data: address
    });
  } catch (error) {
    next(error);
  }
};

export const deleteAddress = async (req, res, next) => {
  try {
    const { id } = req.params;
    const user = await User.findById(req.user._id);
    const address = user.addresses.id(id);

    if (!address) {
      throw new AppError('Address not found.', 404, 'ADDRESS_NOT_FOUND');
    }

    address.deleteOne();
    await user.save();

    res.status(200).json({
      success: true,
      message: 'Address deleted successfully.'
    });
  } catch (error) {
    next(error);
  }
};

export const setDefaultAddress = async (req, res, next) => {
  try {
    const { id } = req.params;
    const user = await User.findById(req.user._id);
    const address = user.addresses.id(id);

    if (!address) {
      throw new AppError('Address not found.', 404, 'ADDRESS_NOT_FOUND');
    }

    user.addresses.forEach((a) => (a.isDefault = a._id.toString() === id));
    await user.save();

    res.status(200).json({
      success: true,
      message: 'Default address updated.',
      data: address
    });
  } catch (error) {
    next(error);
  }
};
