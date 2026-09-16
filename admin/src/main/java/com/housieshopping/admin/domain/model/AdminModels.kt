package com.housieshopping.admin.domain.model

data class AdminProduct(
    val id: String,
    val sku: String = "HS-SKU-100",
    val title: String,
    val description: String,
    val brand: String,
    val categoryName: String,
    val price: Double,
    val mrp: Double,
    val stock: Int,
    val imageUrl: String,
    val isActive: Boolean = true,
    val isFeatured: Boolean = false,
    val grade: String = "Standard",
    val unit: String = "Bag / Unit"
) {
    val discountPercent: Int
        get() = if (mrp > price) (((mrp - price) / mrp) * 100).toInt() else 0
}

data class AdminCategory(
    val id: String,
    val name: String,
    val description: String,
    val iconUrl: String,
    val productCount: Int = 0,
    val isActive: Boolean = true
)

data class AdminOrder(
    val id: String,
    val customerName: String,
    val customerPhone: String,
    val customerEmail: String = "client@example.com",
    val deliveryAddress: String,
    val itemsSummary: String,
    val totalAmount: Double,
    val paymentMode: String,
    val paymentStatus: String = "Paid",
    val orderStatus: String, // Pending, Confirmed, Packing, In Transit, Delivered, Cancelled
    val timestamp: String,
    val trackingNumber: String = "",
    val logisticsPartner: String = "Housie Direct Fleet"
)

data class AdminCustomer(
    val id: String,
    val name: String,
    val email: String,
    val phone: String,
    val registrationDate: String,
    val totalOrders: Int,
    val totalSpent: Double,
    val isActive: Boolean = true
)

data class AdminCoupon(
    val id: String,
    val code: String,
    val discountPercent: Int,
    val maxDiscountAmount: Double,
    val minOrderAmount: Double,
    val validUntil: String,
    val isActive: Boolean = true
)

data class AdminBanner(
    val id: String,
    val title: String,
    val subtitle: String,
    val imageUrl: String,
    val categoryTarget: String,
    val isActive: Boolean = true
)

data class AdminReview(
    val id: String,
    val productName: String,
    val customerName: String,
    val rating: Double,
    val comment: String,
    val date: String,
    val isApproved: Boolean = true
)

data class AuditLog(
    val id: String,
    val adminUser: String,
    val action: String,
    val details: String,
    val timestamp: String
)

data class AdminSupportTicket(
    val id: String,
    val customerName: String,
    val phone: String,
    val subject: String,
    val description: String,
    val status: String, // Open, In Progress, Resolved
    val date: String,
    val adminNotes: String = ""
)

data class DashboardAnalytics(
    val todayRevenue: Double,
    val weeklyRevenue: Double,
    val monthlyRevenue: Double,
    val totalRevenue: Double,
    val todayOrdersCount: Int,
    val pendingOrdersCount: Int,
    val totalProductsCount: Int,
    val lowStockCount: Int,
    val totalCustomersCount: Int
)
