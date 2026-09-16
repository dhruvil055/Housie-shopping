package com.housieshopping.app.navigation

sealed class ScreenRoute(val route: String) {
    // Startup
    object Splash : ScreenRoute("splash")
    object Onboarding : ScreenRoute("onboarding")
    object Login : ScreenRoute("login")
    object Register : ScreenRoute("register")
    object OtpVerification : ScreenRoute("otp_verification/{phone}") {
        fun createRoute(phone: String) = "otp_verification/$phone"
    }
    object ForgotPassword : ScreenRoute("forgot_password")
    object ResetPassword : ScreenRoute("reset_password/{phone}") {
        fun createRoute(phone: String) = "reset_password/$phone"
    }

    // Location
    object LocationPermission : ScreenRoute("location_permission")
    object SelectLocation : ScreenRoute("select_location")
    object MapPicker : ScreenRoute("map_picker")
    object AddressSearch : ScreenRoute("address_search")
    object AddressConfirmation : ScreenRoute("address_confirmation/{lat}/{lng}") {
        fun createRoute(lat: Double, lng: Double) = "address_confirmation/$lat/$lng"
    }

    // Main Hub (Bottom Nav)
    object Home : ScreenRoute("home")
    object Categories : ScreenRoute("categories")
    object Wishlist : ScreenRoute("wishlist")
    object Cart : ScreenRoute("cart")
    object Profile : ScreenRoute("profile")

    // Browsing & Search
    object CategoryProducts : ScreenRoute("category_products/{categoryId}/{categoryName}") {
        fun createRoute(categoryId: String, categoryName: String) = "category_products/$categoryId/$categoryName"
    }
    object Search : ScreenRoute("search")
    object SearchResults : ScreenRoute("search_results/{query}") {
        fun createRoute(query: String) = "search_results/$query"
    }
    object Filters : ScreenRoute("filters")
    object ProductDetails : ScreenRoute("product_details/{productId}") {
        fun createRoute(productId: String) = "product_details/$productId"
    }
    object ProductReviews : ScreenRoute("product_reviews/{productId}") {
        fun createRoute(productId: String) = "product_reviews/$productId"
    }

    // Cart & Checkout
    object SelectAddress : ScreenRoute("select_address")
    object AddAddress : ScreenRoute("add_address")
    object EditAddress : ScreenRoute("edit_address/{addressId}") {
        fun createRoute(addressId: String) = "edit_address/$addressId"
    }
    object DeliveryOptions : ScreenRoute("delivery_options")
    object Checkout : ScreenRoute("checkout")
    object Payment : ScreenRoute("payment/{orderId}/{amount}") {
        fun createRoute(orderId: String, amount: Double) = "payment/$orderId/$amount"
    }
    object PaymentProcessing : ScreenRoute("payment_processing/{orderId}") {
        fun createRoute(orderId: String) = "payment_processing/$orderId"
    }
    object PaymentSuccess : ScreenRoute("payment_success/{orderId}") {
        fun createRoute(orderId: String) = "payment_success/$orderId"
    }
    object PaymentFailed : ScreenRoute("payment_failed/{orderId}") {
        fun createRoute(orderId: String) = "payment_failed/$orderId"
    }
    object OrderConfirmation : ScreenRoute("order_confirmation/{orderId}") {
        fun createRoute(orderId: String) = "order_confirmation/$orderId"
    }

    // Orders
    object Orders : ScreenRoute("orders")
    object OrderDetails : ScreenRoute("order_details/{orderId}") {
        fun createRoute(orderId: String) = "order_details/$orderId"
    }
    object LiveTracking : ScreenRoute("live_tracking/{orderId}") {
        fun createRoute(orderId: String) = "live_tracking/$orderId"
    }
    object CancelOrder : ScreenRoute("cancel_order/{orderId}") {
        fun createRoute(orderId: String) = "cancel_order/$orderId"
    }
    object RefundStatus : ScreenRoute("refund_status/{orderId}") {
        fun createRoute(orderId: String) = "refund_status/$orderId"
    }
    object Invoice : ScreenRoute("invoice/{orderId}") {
        fun createRoute(orderId: String) = "invoice/$orderId"
    }

    // Account & Support
    object EditProfile : ScreenRoute("edit_profile")
    object Notifications : ScreenRoute("notifications")
    object Coupons : ScreenRoute("coupons")
    object SavedAddresses : ScreenRoute("saved_addresses")
    object PaymentMethods : ScreenRoute("payment_methods")
    object Settings : ScreenRoute("settings")
    object HelpSupport : ScreenRoute("help_support")
    object FAQ : ScreenRoute("faq")
    object ContactSupport : ScreenRoute("contact_support")
    object SupportTicket : ScreenRoute("support_ticket")
    object PrivacyPolicy : ScreenRoute("privacy_policy")
    object TermsConditions : ScreenRoute("terms_conditions")
    object About : ScreenRoute("about")
}
