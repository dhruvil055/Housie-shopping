package com.housieshopping.app.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse<T>(
    val success: Boolean,
    val message: String? = null,
    val data: T? = null,
    val code: String? = null
)

// Auth DTOs
@Serializable
data class LoginRequestDto(
    val emailOrPhone: String,
    val password: String
)

@Serializable
data class RegisterRequestDto(
    val name: String,
    val email: String,
    val phone: String,
    val password: String
)

@Serializable
data class VerifyOtpRequestDto(
    val phone: String,
    val otp: String
)

@Serializable
data class RefreshTokenRequestDto(
    val refreshToken: String
)

@Serializable
data class AuthResponseData(
    val userId: String,
    val name: String,
    val email: String,
    val phone: String,
    val role: String? = "CUSTOMER",
    val accessToken: String? = null,
    val refreshToken: String? = null,
    val avatar: String? = null
)

// Product & Catalog DTOs
@Serializable
data class ProductVariantDto(
    val id: String? = null,
    val _id: String? = null,
    val name: String,
    val price: Double,
    val mrp: Double,
    val stock: Int = 0,
    val unit: String? = null,
    val grade: String? = null
)

@Serializable
data class SpecificationDto(
    val key: String,
    val value: String
)

@Serializable
data class ProductResponseDto(
    val id: String? = null,
    val _id: String? = null,
    val sku: String? = null,
    val title: String,
    val description: String? = "",
    val brand: String? = "",
    val categoryId: String? = "",
    val categoryName: String? = "",
    val price: Double,
    val mrp: Double,
    val stock: Int = 0,
    val images: List<String> = emptyList(),
    val deliveryEstimateDays: Int = 2,
    val isFeatured: Boolean = false,
    val isBestSeller: Boolean = false,
    val isDealOfDay: Boolean = false,
    val rating: Double = 4.5,
    val reviewCount: Int = 0,
    val variants: List<ProductVariantDto> = emptyList(),
    val specifications: List<SpecificationDto> = emptyList(),
    val tags: List<String> = emptyList()
)

@Serializable
data class CategoryDto(
    val id: String? = null,
    val _id: String? = null,
    val name: String,
    val slug: String? = null,
    val iconUrl: String? = null,
    val productCount: Int = 0
)

@Serializable
data class BannerDto(
    val id: String? = null,
    val _id: String? = null,
    val title: String,
    val subtitle: String? = null,
    val imageUrl: String,
    val targetType: String? = null,
    val targetId: String? = null
)

@Serializable
data class ReviewDto(
    val id: String? = null,
    val _id: String? = null,
    val userName: String,
    val rating: Double,
    val title: String? = null,
    val comment: String,
    val createdAt: String? = null,
    val isVerifiedPurchase: Boolean = true
)

// Cart DTOs
@Serializable
data class AddToCartRequestDto(
    val productId: String,
    val variantId: String? = null,
    val quantity: Int = 1
)

@Serializable
data class UpdateQuantityRequestDto(
    val quantity: Int
)

@Serializable
data class ApplyCouponRequestDto(
    val couponCode: String
)

@Serializable
data class CartItemDto(
    val id: String,
    val productId: String,
    val title: String,
    val imageUrl: String? = null,
    val brand: String? = null,
    val categoryName: String? = null,
    val unitPrice: Double,
    val unitMrp: Double,
    val quantity: Int,
    val subtotal: Double,
    val totalMrp: Double,
    val availableStock: Int,
    val isOutOfStock: Boolean = false,
    val selectedVariant: ProductVariantDto? = null,
    val savedForLater: Boolean = false
)

@Serializable
data class CartSummaryDto(
    val items: List<CartItemDto> = emptyList(),
    val subtotal: Double = 0.0,
    val totalMrp: Double = 0.0,
    val totalDiscount: Double = 0.0,
    val couponDiscount: Double = 0.0,
    val appliedCouponCode: String? = null,
    val taxAmount: Double = 0.0,
    val deliveryFee: Double = 0.0,
    val grandTotal: Double = 0.0,
    val totalSavings: Double = 0.0
)

// Order & Payment DTOs
@Serializable
data class AddressDto(
    val id: String? = null,
    val _id: String? = null,
    val fullName: String,
    val phone: String,
    val houseFlat: String,
    val street: String? = null,
    val area: String? = null,
    val city: String,
    val state: String,
    val postalCode: String,
    val landmark: String? = null,
    val lat: Double? = 0.0,
    val lng: Double? = 0.0,
    val addressType: String? = "Home",
    val isDefault: Boolean = false
)

@Serializable
data class CreateOrderRequestDto(
    val address: AddressDto,
    val paymentMethod: String
)

@Serializable
data class CreateOrderResponseData(
    val orderId: String,
    val orderNumber: String,
    val totalAmount: Double,
    val currency: String? = "INR",
    val razorpayOrderId: String? = null,
    val razorpayKey: String? = null,
    val isCOD: Boolean = false
)

@Serializable
data class VerifyPaymentRequestDto(
    val orderId: String,
    val razorpayOrderId: String? = null,
    val razorpayPaymentId: String? = null,
    val razorpaySignature: String? = null
)

@Serializable
data class OrderTrackingResponseDto(
    val orderId: String,
    val orderStatus: String,
    val trackingNumber: String? = null,
    val logisticsPartner: String? = null,
    val currentLat: Double = 28.4595,
    val currentLng: Double = 77.0266,
    val destLat: Double = 28.4700,
    val destLng: Double = 77.0300,
    val etaMinutes: Int = 45
)
