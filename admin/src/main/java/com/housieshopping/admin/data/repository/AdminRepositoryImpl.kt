package com.housieshopping.admin.data.repository

import com.housieshopping.admin.data.database.AdminDao
import com.housieshopping.admin.data.database.AuditLogEntity
import com.housieshopping.admin.data.database.BannerEntity
import com.housieshopping.admin.data.database.CategoryEntity
import com.housieshopping.admin.data.database.CouponEntity
import com.housieshopping.admin.data.database.CustomerEntity
import com.housieshopping.admin.data.database.OrderEntity
import com.housieshopping.admin.data.database.ProductEntity
import com.housieshopping.admin.data.database.ReviewEntity
import com.housieshopping.admin.data.database.TicketEntity
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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdminRepositoryImpl @Inject constructor(
    private val adminDao: AdminDao
) : AdminRepository {

    override fun getAllProducts(): Flow<List<AdminProduct>> {
        return adminDao.getAllProducts().map { entities ->
            entities.map {
                AdminProduct(
                    id = it.id,
                    sku = it.sku,
                    title = it.title,
                    description = it.description,
                    brand = it.brand,
                    categoryName = it.categoryName,
                    price = it.price,
                    mrp = it.mrp,
                    stock = it.stock,
                    imageUrl = it.imageUrl,
                    isActive = it.isActive,
                    isFeatured = it.isFeatured,
                    grade = it.grade,
                    unit = it.unit
                )
            }
        }
    }

    override suspend fun saveProduct(product: AdminProduct) {
        val entity = ProductEntity(
            id = product.id.ifEmpty { UUID.randomUUID().toString() },
            sku = product.sku.ifEmpty { "HS-SKU-${System.currentTimeMillis().toString().takeLast(4)}" },
            title = product.title,
            description = product.description,
            brand = product.brand,
            categoryName = product.categoryName,
            price = product.price,
            mrp = product.mrp,
            stock = product.stock,
            imageUrl = product.imageUrl.ifEmpty { "https://images.unsplash.com/photo-1581094794329-c8112a89af12?w=500" },
            isActive = product.isActive,
            isFeatured = product.isFeatured,
            grade = product.grade,
            unit = product.unit
        )
        adminDao.insertProduct(entity)
        logAction("PRODUCT_SAVED", "Saved product '${product.title}' (Price: ₹${product.price}, Stock: ${product.stock})")
    }

    override suspend fun deleteProduct(productId: String) {
        adminDao.deleteProduct(productId)
        logAction("PRODUCT_DELETED", "Deleted product ID $productId")
    }

    override suspend fun toggleStockStatus(productId: String, isActive: Boolean) {
        adminDao.updateStockStatus(productId, isActive)
        logAction("STOCK_TOGGLE", "Toggled stock state of product $productId to $isActive")
    }

    override fun getAllCategories(): Flow<List<AdminCategory>> {
        return adminDao.getAllCategories().map { entities ->
            entities.map {
                AdminCategory(
                    id = it.id,
                    name = it.name,
                    description = it.description,
                    iconUrl = it.iconUrl,
                    productCount = it.productCount,
                    isActive = it.isActive
                )
            }
        }
    }

    override suspend fun saveCategory(category: AdminCategory) {
        val entity = CategoryEntity(
            id = category.id.ifEmpty { UUID.randomUUID().toString() },
            name = category.name,
            description = category.description,
            iconUrl = category.iconUrl,
            productCount = category.productCount,
            isActive = category.isActive
        )
        adminDao.insertCategory(entity)
        logAction("CATEGORY_SAVED", "Saved category '${category.name}'")
    }

    override fun getAllOrders(): Flow<List<AdminOrder>> {
        return adminDao.getAllOrders().map { entities ->
            entities.map {
                AdminOrder(
                    id = it.id,
                    customerName = it.customerName,
                    customerPhone = it.customerPhone,
                    customerEmail = it.customerEmail,
                    deliveryAddress = it.deliveryAddress,
                    itemsSummary = it.itemsSummary,
                    totalAmount = it.totalAmount,
                    paymentMode = it.paymentMode,
                    paymentStatus = it.paymentStatus,
                    orderStatus = it.orderStatus,
                    timestamp = it.timestamp,
                    trackingNumber = it.trackingNumber,
                    logisticsPartner = it.logisticsPartner
                )
            }
        }
    }

    override suspend fun updateOrderStatus(
        orderId: String,
        status: String,
        trackingNumber: String,
        logisticsPartner: String
    ) {
        adminDao.updateOrderStatus(orderId, status, trackingNumber, logisticsPartner)
        logAction("ORDER_STATUS_UPDATE", "Order $orderId updated to $status ($logisticsPartner, Tracking: $trackingNumber)")
    }

    override fun getAllCustomers(): Flow<List<AdminCustomer>> {
        return adminDao.getAllCustomers().map { entities ->
            entities.map {
                AdminCustomer(
                    id = it.id,
                    name = it.name,
                    email = it.email,
                    phone = it.phone,
                    registrationDate = it.registrationDate,
                    totalOrders = it.totalOrders,
                    totalSpent = it.totalSpent,
                    isActive = it.isActive
                )
            }
        }
    }

    override suspend fun toggleCustomerStatus(customerId: String, isActive: Boolean) {
        adminDao.toggleCustomerStatus(customerId, isActive)
        logAction("CUSTOMER_STATUS_TOGGLE", "Toggled customer $customerId status to $isActive")
    }

    override fun getAllCoupons(): Flow<List<AdminCoupon>> {
        return adminDao.getAllCoupons().map { entities ->
            entities.map {
                AdminCoupon(
                    id = it.id,
                    code = it.code,
                    discountPercent = it.discountPercent,
                    maxDiscountAmount = it.maxDiscountAmount,
                    minOrderAmount = it.minOrderAmount,
                    validUntil = it.validUntil,
                    isActive = it.isActive
                )
            }
        }
    }

    override suspend fun saveCoupon(coupon: AdminCoupon) {
        val entity = CouponEntity(
            id = coupon.id.ifEmpty { UUID.randomUUID().toString() },
            code = coupon.code.uppercase(),
            discountPercent = coupon.discountPercent,
            maxDiscountAmount = coupon.maxDiscountAmount,
            minOrderAmount = coupon.minOrderAmount,
            validUntil = coupon.validUntil,
            isActive = coupon.isActive
        )
        adminDao.insertCoupon(entity)
        logAction("COUPON_SAVED", "Saved coupon promo code ${coupon.code}")
    }

    override suspend fun toggleCoupon(couponId: String, isActive: Boolean) {
        adminDao.toggleCouponStatus(couponId, isActive)
        logAction("COUPON_TOGGLE", "Toggled coupon $couponId to $isActive")
    }

    override fun getAllBanners(): Flow<List<AdminBanner>> {
        return adminDao.getAllBanners().map { entities ->
            entities.map {
                AdminBanner(
                    id = it.id,
                    title = it.title,
                    subtitle = it.subtitle,
                    imageUrl = it.imageUrl,
                    categoryTarget = it.categoryTarget,
                    isActive = it.isActive
                )
            }
        }
    }

    override suspend fun saveBanner(banner: AdminBanner) {
        val entity = BannerEntity(
            id = banner.id.ifEmpty { UUID.randomUUID().toString() },
            title = banner.title,
            subtitle = banner.subtitle,
            imageUrl = banner.imageUrl,
            categoryTarget = banner.categoryTarget,
            isActive = banner.isActive
        )
        adminDao.insertBanner(entity)
        logAction("BANNER_SAVED", "Saved promotion banner: ${banner.title}")
    }

    override suspend fun deleteBanner(bannerId: String) {
        adminDao.deleteBanner(bannerId)
        logAction("BANNER_DELETED", "Deleted banner $bannerId")
    }

    override fun getAllReviews(): Flow<List<AdminReview>> {
        return adminDao.getAllReviews().map { entities ->
            entities.map {
                AdminReview(
                    id = it.id,
                    productName = it.productName,
                    customerName = it.customerName,
                    rating = it.rating,
                    comment = it.comment,
                    date = it.date,
                    isApproved = it.isApproved
                )
            }
        }
    }

    override suspend fun deleteReview(reviewId: String) {
        adminDao.deleteReview(reviewId)
        logAction("REVIEW_DELETED", "Deleted review $reviewId")
    }

    override fun getAllAuditLogs(): Flow<List<AuditLog>> {
        return adminDao.getAllAuditLogs().map { entities ->
            entities.map {
                AuditLog(
                    id = it.id,
                    adminUser = it.adminUser,
                    action = it.action,
                    details = it.details,
                    timestamp = it.timestamp
                )
            }
        }
    }

    override suspend fun logAction(action: String, details: String) {
        val dateStr = SimpleDateFormat("dd MMM yyyy, hh:mm:ss a", Locale.getDefault()).format(Date())
        val entity = AuditLogEntity(
            id = UUID.randomUUID().toString(),
            adminUser = "Main Store Manager",
            action = action,
            details = details,
            timestamp = dateStr
        )
        adminDao.insertAuditLog(entity)
    }

    override fun getAllSupportTickets(): Flow<List<AdminSupportTicket>> {
        return adminDao.getAllSupportTickets().map { entities ->
            entities.map {
                AdminSupportTicket(
                    id = it.id,
                    customerName = it.customerName,
                    phone = it.phone,
                    subject = it.subject,
                    description = it.description,
                    status = it.status,
                    date = it.date,
                    adminNotes = it.adminNotes
                )
            }
        }
    }

    override suspend fun updateTicket(ticketId: String, status: String, notes: String) {
        adminDao.updateTicketStatus(ticketId, status, notes)
        logAction("TICKET_UPDATED", "Support ticket $ticketId updated to $status")
    }

    override suspend fun seedInitialDataIfEmpty() {
        val currentProducts = adminDao.getAllProducts().first()
        if (currentProducts.isEmpty()) {
            val sampleProducts = listOf(
                ProductEntity("p101", "SKU-CEM-53", "UltraTech Super OPC 53 Grade Cement", "High strength cement for slab & column construction.", "UltraTech", "Cement", 380.0, 420.0, 150, "https://images.unsplash.com/photo-1581094794329-c8112a89af12?w=500", true, true, "53 Grade", "50kg Bag"),
                ProductEntity("p102", "SKU-STL-12", "Tata Tiscon 550SD TMT Rebar (12mm)", "Super ductile seismic resistant steel rebar.", "Tata Tiscon", "Steel & TMT Bars", 620.0, 710.0, 200, "https://images.unsplash.com/photo-1504307651254-35680f356dfd?w=500", true, true, "Fe 550D", "12mm Rod"),
                ProductEntity("p103", "SKU-PNT-20L", "Asian Paints Apex Ultima Exterior Emulsion (20L)", "Advanced dust resistant weatherproof paint.", "Asian Paints", "Paints", 4850.0, 5600.0, 45, "https://images.unsplash.com/photo-1589939705384-5185137a7f0f?w=500", true, false, "Premium Grade", "20 Litre Bucket"),
                ProductEntity("p104", "SKU-BRK-RED", "Red Clay Bricks (First Class Machine Molded)", "High compressive strength red bricks.", "Housie Local", "Bricks & Blocks", 9.5, 12.0, 5000, "https://images.unsplash.com/photo-1590069261209-f8e9b8642343?w=500", true, false, "Class 1", "Per Piece")
            )
            sampleProducts.forEach { adminDao.insertProduct(it) }

            val sampleCategories = listOf(
                CategoryEntity("cat1", "Cement", "OPC & PPC Cements", "https://images.unsplash.com/photo-1581094794329-c8112a89af12?w=500", 12, true),
                CategoryEntity("cat2", "Steel & TMT Bars", "TMT Rebars & Steel Rods", "https://images.unsplash.com/photo-1504307651254-35680f356dfd?w=500", 18, true),
                CategoryEntity("cat3", "Paints", "Interior & Exterior Paints", "https://images.unsplash.com/photo-1589939705384-5185137a7f0f?w=500", 24, true)
            )
            sampleCategories.forEach { adminDao.insertCategory(it) }

            val sampleOrders = listOf(
                OrderEntity("ORD-2026-9041", "DLF Constructions", "+91 98765 43210", "dlf@constructions.com", "DLF Phase 2, Site 4, Gurugram", "50 Bags UltraTech OPC 53 + 2 TMT Bundles", 28400.0, "UPI Paid", "Paid", "Pending", "01 Sep 2026, 11:30 AM", "TRK-9041-IN", "Housie Direct Fleet"),
                OrderEntity("ORD-2026-9040", "Rajesh Builders", "+91 98112 33445", "rajesh.b@builder.in", "Sector 57, Site 12, Noida", "1000 Red Clay Bricks (First Class)", 9500.0, "Cash on Delivery", "Pending COD", "Packing", "01 Sep 2026, 09:15 AM", "TRK-9040-IN", "Housie Direct Fleet"),
                OrderEntity("ORD-2026-9038", "Asian Infrastructure", "+91 99100 88776", "infra@asian.com", "Golf Course Road, Site 8, Gurugram", "20L Asian Paints Exterior Emulsion (5 Tubs)", 24250.0, "Bank Transfer", "Paid", "In Transit", "31 Aug 2026, 04:45 PM", "TRK-9038-IN", "Delhivery Logistics")
            )
            sampleOrders.forEach { adminDao.insertOrder(it) }

            val sampleCustomers = listOf(
                CustomerEntity("c101", "DLF Constructions", "dlf@constructions.com", "+91 98765 43210", "12 Jan 2026", 14, 284000.0, true),
                CustomerEntity("c102", "Rajesh Builders", "rajesh.b@builder.in", "+91 98112 33445", "05 Mar 2026", 8, 142000.0, true),
                CustomerEntity("c103", "Asian Infrastructure", "infra@asian.com", "+91 99100 88776", "18 Feb 2026", 22, 580000.0, true)
            )
            sampleCustomers.forEach { adminDao.insertCustomer(it) }

            val sampleCoupons = listOf(
                CouponEntity("cp1", "BUILD500", 10, 500.0, 3000.0, "31 Dec 2026", true),
                CouponEntity("cp2", "CEMENTGURU", 15, 1500.0, 10000.0, "15 Nov 2026", true),
                CouponEntity("cp3", "STEELDEAL", 12, 2500.0, 20000.0, "30 Sep 2026", true)
            )
            sampleCoupons.forEach { adminDao.insertCoupon(it) }

            val sampleBanners = listOf(
                BannerEntity("b1", "Mega Cement & TMT Sale", "Up to 25% Off on Bulk Orders + Free Delivery", "https://images.unsplash.com/photo-1541888946425-d0fbb186a5b2?w=800", "cat1", true)
            )
            sampleBanners.forEach { adminDao.insertBanner(it) }

            val sampleReviews = listOf(
                ReviewEntity("r1", "UltraTech Super OPC 53 Grade Cement", "Contractor Amit", 5.0, "Excellent setting time and strength for roof slab.", "28 Aug 2026", true),
                ReviewEntity("r2", "Tata Tiscon 550SD TMT Rebar (12mm)", "Sharma Construction", 4.8, "Genuine Tata steel with test certificate provided.", "25 Aug 2026", true)
            )
            sampleReviews.forEach { adminDao.insertReview(it) }

            val sampleTickets = listOf(
                TicketEntity("t101", "Vikram Singh (Contractor)", "+91 98711 22334", "Bulk cement delivery schedule", "Need 200 bags delivered directly to site before 8 AM tomorrow.", "Open", "01 Sep 2026", ""),
                TicketEntity("t102", "Sunil Construction", "+91 98990 11223", "GST Invoice mismatch", "Need updated GSTIN on invoice INV-2026-ord_902.", "In Progress", "31 Aug 2026", "Requested corrected GSTIN from user")
            )
            sampleTickets.forEach { adminDao.insertTicket(it) }

            logAction("SYSTEM_INIT", "Admin database seeded with initial store catalog, orders, categories & coupons.")
        }
    }
}
