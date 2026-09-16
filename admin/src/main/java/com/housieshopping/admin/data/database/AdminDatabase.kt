package com.housieshopping.admin.data.database

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

// Entities
@Entity(tableName = "admin_products")
data class ProductEntity(
    @PrimaryKey val id: String,
    val sku: String,
    val title: String,
    val description: String,
    val brand: String,
    val categoryName: String,
    val price: Double,
    val mrp: Double,
    val stock: Int,
    val imageUrl: String,
    val isActive: Boolean,
    val isFeatured: Boolean,
    val grade: String,
    val unit: String
)

@Entity(tableName = "admin_categories")
data class CategoryEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val iconUrl: String,
    val productCount: Int,
    val isActive: Boolean
)

@Entity(tableName = "admin_orders")
data class OrderEntity(
    @PrimaryKey val id: String,
    val customerName: String,
    val customerPhone: String,
    val customerEmail: String,
    val deliveryAddress: String,
    val itemsSummary: String,
    val totalAmount: Double,
    val paymentMode: String,
    val paymentStatus: String,
    val orderStatus: String,
    val timestamp: String,
    val trackingNumber: String,
    val logisticsPartner: String
)

@Entity(tableName = "admin_customers")
data class CustomerEntity(
    @PrimaryKey val id: String,
    val name: String,
    val email: String,
    val phone: String,
    val registrationDate: String,
    val totalOrders: Int,
    val totalSpent: Double,
    val isActive: Boolean
)

@Entity(tableName = "admin_coupons")
data class CouponEntity(
    @PrimaryKey val id: String,
    val code: String,
    val discountPercent: Int,
    val maxDiscountAmount: Double,
    val minOrderAmount: Double,
    val validUntil: String,
    val isActive: Boolean
)

@Entity(tableName = "admin_banners")
data class BannerEntity(
    @PrimaryKey val id: String,
    val title: String,
    val subtitle: String,
    val imageUrl: String,
    val categoryTarget: String,
    val isActive: Boolean
)

@Entity(tableName = "admin_reviews")
data class ReviewEntity(
    @PrimaryKey val id: String,
    val productName: String,
    val customerName: String,
    val rating: Double,
    val comment: String,
    val date: String,
    val isApproved: Boolean
)

@Entity(tableName = "admin_audit_logs")
data class AuditLogEntity(
    @PrimaryKey val id: String,
    val adminUser: String,
    val action: String,
    val details: String,
    val timestamp: String
)

@Entity(tableName = "admin_support_tickets")
data class TicketEntity(
    @PrimaryKey val id: String,
    val customerName: String,
    val phone: String,
    val subject: String,
    val description: String,
    val status: String,
    val date: String,
    val adminNotes: String
)

// DAO
@Dao
interface AdminDao {
    // Products
    @Query("SELECT * FROM admin_products ORDER BY title ASC")
    fun getAllProducts(): Flow<List<ProductEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity)

    @Query("DELETE FROM admin_products WHERE id = :productId")
    suspend fun deleteProduct(productId: String)

    @Query("UPDATE admin_products SET isActive = :isActive WHERE id = :productId")
    suspend fun updateStockStatus(productId: String, isActive: Boolean)

    // Categories
    @Query("SELECT * FROM admin_categories ORDER BY name ASC")
    fun getAllCategories(): Flow<List<CategoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: CategoryEntity)

    // Orders
    @Query("SELECT * FROM admin_orders ORDER BY timestamp DESC")
    fun getAllOrders(): Flow<List<OrderEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity)

    @Query("UPDATE admin_orders SET orderStatus = :status, trackingNumber = :trackingNo, logisticsPartner = :logistics WHERE id = :orderId")
    suspend fun updateOrderStatus(orderId: String, status: String, trackingNo: String, logistics: String)

    // Customers
    @Query("SELECT * FROM admin_customers ORDER BY name ASC")
    fun getAllCustomers(): Flow<List<CustomerEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomer(customer: CustomerEntity)

    @Query("UPDATE admin_customers SET isActive = :isActive WHERE id = :customerId")
    suspend fun toggleCustomerStatus(customerId: String, isActive: Boolean)

    // Coupons
    @Query("SELECT * FROM admin_coupons ORDER BY code ASC")
    fun getAllCoupons(): Flow<List<CouponEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCoupon(coupon: CouponEntity)

    @Query("UPDATE admin_coupons SET isActive = :isActive WHERE id = :couponId")
    suspend fun toggleCouponStatus(couponId: String, isActive: Boolean)

    // Banners
    @Query("SELECT * FROM admin_banners")
    fun getAllBanners(): Flow<List<BannerEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBanner(banner: BannerEntity)

    @Query("DELETE FROM admin_banners WHERE id = :bannerId")
    suspend fun deleteBanner(bannerId: String)

    // Reviews
    @Query("SELECT * FROM admin_reviews ORDER BY date DESC")
    fun getAllReviews(): Flow<List<ReviewEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReview(review: ReviewEntity)

    @Query("DELETE FROM admin_reviews WHERE id = :reviewId")
    suspend fun deleteReview(reviewId: String)

    // Audit Logs
    @Query("SELECT * FROM admin_audit_logs ORDER BY timestamp DESC")
    fun getAllAuditLogs(): Flow<List<AuditLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuditLog(log: AuditLogEntity)

    // Support Tickets
    @Query("SELECT * FROM admin_support_tickets ORDER BY date DESC")
    fun getAllSupportTickets(): Flow<List<TicketEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTicket(ticket: TicketEntity)

    @Query("UPDATE admin_support_tickets SET status = :status, adminNotes = :notes WHERE id = :ticketId")
    suspend fun updateTicketStatus(ticketId: String, status: String, notes: String)
}

@Database(
    entities = [
        ProductEntity::class,
        CategoryEntity::class,
        OrderEntity::class,
        CustomerEntity::class,
        CouponEntity::class,
        BannerEntity::class,
        ReviewEntity::class,
        AuditLogEntity::class,
        TicketEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AdminDatabase : RoomDatabase() {
    abstract fun adminDao(): AdminDao
}
