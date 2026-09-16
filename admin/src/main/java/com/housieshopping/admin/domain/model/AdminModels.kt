package com.housieshopping.admin.domain.model

import com.google.gson.annotations.SerializedName

data class AdminProduct(
    @SerializedName("_id", alternate = ["id"])
    val id: String = "",
    val sku: String = "HS-SKU-100",
    val title: String = "",
    val description: String = "",
    val brand: String = "",
    val categoryName: String = "",
    val price: Double = 0.0,
    val mrp: Double = 0.0,
    val stock: Int = 0,
    val imageUrl: String = "",
    val isActive: Boolean = true,
    val isFeatured: Boolean = false,
    val grade: String = "Standard",
    val unit: String = "Bag / Unit"
) {
    val discountPercent: Int
        get() = if (mrp > price) (((mrp - price) / mrp) * 100).toInt() else 0
}

data class AdminCategory(
    @SerializedName("_id", alternate = ["id"])
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val iconUrl: String = "",
    val productCount: Int = 0,
    val isActive: Boolean = true
)

data class AdminOrder(
    @SerializedName("_id", alternate = ["id"])
    val id: String = "",
    val customerName: String = "",
    val customerPhone: String = "",
    val customerEmail: String = "client@example.com",
    val deliveryAddress: String = "",
    val itemsSummary: String = "",
    val totalAmount: Double = 0.0,
    val paymentMode: String = "Online Paid",
    val paymentStatus: String = "Paid",
    val orderStatus: String = "Pending", // Pending, Confirmed, Packing, In Transit, Delivered, Cancelled
    val timestamp: String = "",
    val trackingNumber: String = "",
    val logisticsPartner: String = "Housie Direct Fleet"
)

data class AdminCustomer(
    @SerializedName("_id", alternate = ["id"])
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val registrationDate: String = "",
    val totalOrders: Int = 0,
    val totalSpent: Double = 0.0,
    val isActive: Boolean = true
)

data class AdminCoupon(
    @SerializedName("_id", alternate = ["id"])
    val id: String = "",
    val code: String = "",
    val discountPercent: Int = 0,
    val maxDiscountAmount: Double = 0.0,
    val minOrderAmount: Double = 0.0,
    val validUntil: String = "",
    val isActive: Boolean = true
)

data class AdminBanner(
    @SerializedName("_id", alternate = ["id"])
    val id: String = "",
    val title: String = "",
    val subtitle: String = "",
    val imageUrl: String = "",
    val categoryTarget: String = "",
    val isActive: Boolean = true
)

data class AdminReview(
    @SerializedName("_id", alternate = ["id"])
    val id: String = "",
    val productName: String = "",
    val customerName: String = "",
    val rating: Double = 5.0,
    val comment: String = "",
    val date: String = "",
    val isApproved: Boolean = true
)

data class AuditLog(
    @SerializedName("_id", alternate = ["id"])
    val id: String = "",
    val adminUser: String = "",
    val action: String = "",
    val details: String = "",
    val timestamp: String = ""
)

data class AdminSupportTicket(
    @SerializedName("_id", alternate = ["id"])
    val id: String = "",
    val customerName: String = "",
    val phone: String = "",
    val subject: String = "",
    val description: String = "",
    val status: String = "Open", // Open, In Progress, Resolved
    val date: String = "",
    val adminNotes: String = ""
)

data class DashboardAnalytics(
    val todayRevenue: Double = 0.0,
    val weeklyRevenue: Double = 0.0,
    val monthlyRevenue: Double = 0.0,
    val totalRevenue: Double = 0.0,
    val todayOrdersCount: Int = 0,
    val pendingOrdersCount: Int = 0,
    val totalProductsCount: Int = 0,
    val lowStockCount: Int = 0,
    val totalCustomersCount: Int = 0
)

