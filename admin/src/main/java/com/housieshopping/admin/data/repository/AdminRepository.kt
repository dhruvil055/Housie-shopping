package com.housieshopping.admin.data.repository

import com.housieshopping.admin.domain.model.AdminBanner
import com.housieshopping.admin.domain.model.AdminCategory
import com.housieshopping.admin.domain.model.AdminCoupon
import com.housieshopping.admin.domain.model.AdminCustomer
import com.housieshopping.admin.domain.model.AdminOrder
import com.housieshopping.admin.domain.model.AdminProduct
import com.housieshopping.admin.domain.model.AdminReview
import com.housieshopping.admin.domain.model.AdminSupportTicket
import com.housieshopping.admin.domain.model.AuditLog
import kotlinx.coroutines.flow.Flow

interface AdminRepository {
    // Products
    fun getAllProducts(): Flow<List<AdminProduct>>
    suspend fun saveProduct(product: AdminProduct)
    suspend fun deleteProduct(productId: String)
    suspend fun toggleStockStatus(productId: String, isActive: Boolean)

    // Categories
    fun getAllCategories(): Flow<List<AdminCategory>>
    suspend fun saveCategory(category: AdminCategory)

    // Orders
    fun getAllOrders(): Flow<List<AdminOrder>>
    suspend fun updateOrderStatus(orderId: String, status: String, trackingNumber: String, logisticsPartner: String)

    // Customers
    fun getAllCustomers(): Flow<List<AdminCustomer>>
    suspend fun toggleCustomerStatus(customerId: String, isActive: Boolean)

    // Coupons
    fun getAllCoupons(): Flow<List<AdminCoupon>>
    suspend fun saveCoupon(coupon: AdminCoupon)
    suspend fun toggleCoupon(couponId: String, isActive: Boolean)

    // Banners
    fun getAllBanners(): Flow<List<AdminBanner>>
    suspend fun saveBanner(banner: AdminBanner)
    suspend fun deleteBanner(bannerId: String)

    // Reviews
    fun getAllReviews(): Flow<List<AdminReview>>
    suspend fun deleteReview(reviewId: String)

    // Audit Trail
    fun getAllAuditLogs(): Flow<List<AuditLog>>
    suspend fun logAction(action: String, details: String)

    // Support Desk
    fun getAllSupportTickets(): Flow<List<AdminSupportTicket>>
    suspend fun updateTicket(ticketId: String, status: String, notes: String)

    // Database Initialization
    suspend fun seedInitialDataIfEmpty()
}
