package com.housieshopping.admin.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.housieshopping.admin.data.preferences.AdminPreferences
import com.housieshopping.admin.data.repository.AdminRepository
import com.housieshopping.admin.presentation.analytics.AdminAnalyticsScreen
import com.housieshopping.admin.presentation.analytics.AdminAnalyticsViewModel
import com.housieshopping.admin.presentation.audit.AdminAuditLogsScreen
import com.housieshopping.admin.presentation.audit.AdminAuditViewModel
import com.housieshopping.admin.presentation.auth.AdminLoginScreen
import com.housieshopping.admin.presentation.auth.AdminLoginViewModel
import com.housieshopping.admin.presentation.banners.AdminBannersScreen
import com.housieshopping.admin.presentation.banners.AdminBannersViewModel
import com.housieshopping.admin.presentation.categories.AdminCategoriesScreen
import com.housieshopping.admin.presentation.categories.AdminCategoriesViewModel
import com.housieshopping.admin.presentation.coupons.AdminCouponsScreen
import com.housieshopping.admin.presentation.coupons.AdminCouponsViewModel
import com.housieshopping.admin.presentation.customers.AdminCustomersScreen
import com.housieshopping.admin.presentation.customers.AdminCustomersViewModel
import com.housieshopping.admin.presentation.dashboard.AdminDashboardScreen
import com.housieshopping.admin.presentation.dashboard.AdminDashboardViewModel
import com.housieshopping.admin.presentation.inventory.AdminAddEditProductScreen
import com.housieshopping.admin.presentation.inventory.AdminInventoryViewModel
import com.housieshopping.admin.presentation.inventory.AdminProductsScreen
import com.housieshopping.admin.presentation.notifications.AdminNotificationCenterScreen
import com.housieshopping.admin.presentation.notifications.AdminNotificationsViewModel
import com.housieshopping.admin.presentation.orders.AdminOrderDetailsScreen
import com.housieshopping.admin.presentation.orders.AdminOrdersScreen
import com.housieshopping.admin.presentation.orders.AdminOrdersViewModel
import com.housieshopping.admin.presentation.reviews.AdminReviewsScreen
import com.housieshopping.admin.presentation.reviews.AdminReviewsViewModel
import com.housieshopping.admin.presentation.settings.AdminSettingsScreen
import com.housieshopping.admin.presentation.settings.AdminSettingsViewModel
import com.housieshopping.admin.presentation.splash.AdminSplashScreen
import com.housieshopping.admin.presentation.support.AdminSupportDeskScreen
import com.housieshopping.admin.presentation.support.AdminSupportViewModel

@Composable
fun AdminNavGraph(
    adminPreferences: AdminPreferences,
    adminRepository: AdminRepository,
    navController: NavHostController = rememberNavController(),
    startDestination: String = AdminScreenRoute.Splash.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Splash
        composable(AdminScreenRoute.Splash.route) {
            AdminSplashScreen(
                adminPreferences = adminPreferences,
                adminRepository = adminRepository,
                onNavigate = { targetRoute ->
                    navController.navigate(targetRoute) {
                        popUpTo(AdminScreenRoute.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        // Authentication
        composable(AdminScreenRoute.Login.route) {
            val viewModel: AdminLoginViewModel = hiltViewModel()
            AdminLoginScreen(
                viewModel = viewModel,
                onLoginSuccess = {
                    navController.navigate(AdminScreenRoute.Dashboard.route) {
                        popUpTo(AdminScreenRoute.Login.route) { inclusive = true }
                    }
                }
            )
        }

        // Dashboard
        composable(AdminScreenRoute.Dashboard.route) {
            val viewModel: AdminDashboardViewModel = hiltViewModel()
            AdminDashboardScreen(
                viewModel = viewModel,
                onNavigate = { route ->
                    if (route == AdminScreenRoute.Login.route) {
                        navController.navigate(AdminScreenRoute.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    } else {
                        navController.navigate(route)
                    }
                }
            )
        }

        // Inventory
        composable(AdminScreenRoute.Products.route) {
            val viewModel: AdminInventoryViewModel = hiltViewModel()
            AdminProductsScreen(
                viewModel = viewModel,
                onNavigate = { route ->
                    if (route == "-1") navController.popBackStack() else navController.navigate(route)
                }
            )
        }

        composable(
            route = AdminScreenRoute.AddEditProduct.route,
            arguments = listOf(navArgument("productId") { type = NavType.StringType })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: "new"
            val viewModel: AdminInventoryViewModel = hiltViewModel()
            AdminAddEditProductScreen(
                productId = productId,
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Categories
        composable(AdminScreenRoute.Categories.route) {
            val viewModel: AdminCategoriesViewModel = hiltViewModel()
            AdminCategoriesScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Orders
        composable(AdminScreenRoute.Orders.route) {
            val viewModel: AdminOrdersViewModel = hiltViewModel()
            AdminOrdersScreen(
                viewModel = viewModel,
                onNavigate = { route ->
                    if (route == "-1") navController.popBackStack() else navController.navigate(route)
                }
            )
        }

        composable(
            route = AdminScreenRoute.OrderDetails.route,
            arguments = listOf(navArgument("orderId") { type = NavType.StringType })
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
            val viewModel: AdminOrdersViewModel = hiltViewModel()
            AdminOrderDetailsScreen(
                orderId = orderId,
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Customers
        composable(AdminScreenRoute.Customers.route) {
            val viewModel: AdminCustomersViewModel = hiltViewModel()
            AdminCustomersScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Coupons
        composable(AdminScreenRoute.Coupons.route) {
            val viewModel: AdminCouponsViewModel = hiltViewModel()
            AdminCouponsScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Banners
        composable(AdminScreenRoute.Banners.route) {
            val viewModel: AdminBannersViewModel = hiltViewModel()
            AdminBannersScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Reviews
        composable(AdminScreenRoute.Reviews.route) {
            val viewModel: AdminReviewsViewModel = hiltViewModel()
            AdminReviewsScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Support Desk
        composable(AdminScreenRoute.SupportDesk.route) {
            val viewModel: AdminSupportViewModel = hiltViewModel()
            AdminSupportDeskScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Notifications
        composable(AdminScreenRoute.NotificationCenter.route) {
            val viewModel: AdminNotificationsViewModel = hiltViewModel()
            AdminNotificationCenterScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Analytics
        composable(AdminScreenRoute.Analytics.route) {
            val viewModel: AdminAnalyticsViewModel = hiltViewModel()
            AdminAnalyticsScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Audit Logs
        composable(AdminScreenRoute.AuditLogs.route) {
            val viewModel: AdminAuditViewModel = hiltViewModel()
            AdminAuditLogsScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Settings
        composable(AdminScreenRoute.Settings.route) {
            val viewModel: AdminSettingsViewModel = hiltViewModel()
            AdminSettingsScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
