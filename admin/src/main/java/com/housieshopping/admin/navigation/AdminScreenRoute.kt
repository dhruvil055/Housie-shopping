package com.housieshopping.admin.navigation

sealed class AdminScreenRoute(val route: String) {
    object Splash : AdminScreenRoute("admin_splash")
    object Login : AdminScreenRoute("admin_login")
    object Dashboard : AdminScreenRoute("admin_dashboard")
    object Products : AdminScreenRoute("admin_products")
    object AddEditProduct : AdminScreenRoute("admin_add_edit_product/{productId}") {
        fun createRoute(productId: String = "new") = "admin_add_edit_product/$productId"
    }
    object Categories : AdminScreenRoute("admin_categories")
    object Orders : AdminScreenRoute("admin_orders")
    object OrderDetails : AdminScreenRoute("admin_order_details/{orderId}") {
        fun createRoute(orderId: String) = "admin_order_details/$orderId"
    }
    object Customers : AdminScreenRoute("admin_customers")
    object Coupons : AdminScreenRoute("admin_coupons")
    object Banners : AdminScreenRoute("admin_banners")
    object Reviews : AdminScreenRoute("admin_reviews")
    object SupportDesk : AdminScreenRoute("admin_support_desk")
    object NotificationCenter : AdminScreenRoute("admin_notification_center")
    object Analytics : AdminScreenRoute("admin_analytics")
    object AuditLogs : AdminScreenRoute("admin_audit_logs")
    object Settings : AdminScreenRoute("admin_settings")
}
