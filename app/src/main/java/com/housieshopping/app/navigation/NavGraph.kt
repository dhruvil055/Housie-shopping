package com.housieshopping.app.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.housieshopping.app.presentation.address.AddEditAddressScreen
import com.housieshopping.app.presentation.address.SavedAddressesScreen
import com.housieshopping.app.presentation.address.SelectAddressScreen
import com.housieshopping.app.presentation.auth.ForgotPasswordScreen
import com.housieshopping.app.presentation.auth.LoginScreen
import com.housieshopping.app.presentation.auth.OtpScreen
import com.housieshopping.app.presentation.auth.RegisterScreen
import com.housieshopping.app.presentation.auth.ResetPasswordScreen
import com.housieshopping.app.presentation.cart.CartScreen
import com.housieshopping.app.presentation.categories.CategoriesScreen
import com.housieshopping.app.presentation.categories.CategoryProductsScreen
import com.housieshopping.app.presentation.checkout.CheckoutScreen
import com.housieshopping.app.presentation.checkout.DeliveryOptionsScreen
import com.housieshopping.app.presentation.checkout.OrderConfirmationScreen
import com.housieshopping.app.presentation.checkout.PaymentFailedScreen
import com.housieshopping.app.presentation.checkout.PaymentProcessingScreen
import com.housieshopping.app.presentation.checkout.PaymentScreen
import com.housieshopping.app.presentation.checkout.PaymentSuccessScreen
import com.housieshopping.app.presentation.coupons.CouponsScreen
import com.housieshopping.app.presentation.home.HomeScreen
import com.housieshopping.app.presentation.location.AddressConfirmationScreen
import com.housieshopping.app.presentation.location.AddressSearchScreen
import com.housieshopping.app.presentation.location.LocationPermissionScreen
import com.housieshopping.app.presentation.location.MapPickerScreen
import com.housieshopping.app.presentation.location.SelectLocationScreen
import com.housieshopping.app.presentation.notifications.NotificationsScreen
import com.housieshopping.app.presentation.onboarding.OnboardingScreen
import com.housieshopping.app.presentation.orders.CancelOrderScreen
import com.housieshopping.app.presentation.orders.InvoiceScreen
import com.housieshopping.app.presentation.orders.OrderDetailsScreen
import com.housieshopping.app.presentation.orders.OrdersScreen
import com.housieshopping.app.presentation.orders.RefundStatusScreen
import com.housieshopping.app.presentation.orders.TrackOrderScreen
import com.housieshopping.app.presentation.product.ProductDetailsScreen
import com.housieshopping.app.presentation.product.ProductReviewsScreen
import com.housieshopping.app.presentation.profile.EditProfileScreen
import com.housieshopping.app.presentation.profile.ProfileScreen
import com.housieshopping.app.presentation.search.FiltersScreen
import com.housieshopping.app.presentation.search.SearchScreen
import com.housieshopping.app.presentation.search.SearchResultsScreen
import com.housieshopping.app.presentation.settings.AboutScreen
import com.housieshopping.app.presentation.settings.PaymentMethodsScreen
import com.housieshopping.app.presentation.settings.PrivacyPolicyScreen
import com.housieshopping.app.presentation.settings.SettingsScreen
import com.housieshopping.app.presentation.settings.TermsConditionsScreen
import com.housieshopping.app.presentation.splash.SplashScreen
import com.housieshopping.app.presentation.support.ContactSupportScreen
import com.housieshopping.app.presentation.support.FAQScreen
import com.housieshopping.app.presentation.support.HelpSupportScreen
import com.housieshopping.app.presentation.support.SupportTicketScreen
import com.housieshopping.app.presentation.wishlist.WishlistScreen
import com.housieshopping.app.ui.components.BottomNavBar

@Composable
fun HousieNavGraph(
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val bottomNavRoutes = listOf(
        ScreenRoute.Home.route,
        ScreenRoute.Categories.route,
        ScreenRoute.Wishlist.route,
        ScreenRoute.Cart.route,
        ScreenRoute.Profile.route
    )

    val showBottomBar = currentRoute in bottomNavRoutes

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavBar(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(ScreenRoute.Home.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = ScreenRoute.Splash.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            // Startup
            composable(ScreenRoute.Splash.route) {
                SplashScreen(onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo(ScreenRoute.Splash.route) { inclusive = true }
                    }
                })
            }
            composable(ScreenRoute.Onboarding.route) {
                OnboardingScreen(onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo(ScreenRoute.Onboarding.route) { inclusive = true }
                    }
                })
            }
            composable(ScreenRoute.Login.route) {
                LoginScreen(onNavigate = { navController.navigate(it) })
            }
            composable(ScreenRoute.Register.route) {
                RegisterScreen(onNavigate = { navController.navigate(it) })
            }
            composable(
                route = ScreenRoute.OtpVerification.route,
                arguments = listOf(navArgument("phone") { type = NavType.StringType })
            ) { backStackEntry ->
                val phone = backStackEntry.arguments?.getString("phone") ?: ""
                OtpScreen(phone = phone, onNavigate = { navController.navigate(it) })
            }
            composable(ScreenRoute.ForgotPassword.route) {
                ForgotPasswordScreen(onNavigate = { navController.navigate(it) })
            }
            composable(
                route = ScreenRoute.ResetPassword.route,
                arguments = listOf(navArgument("phone") { type = NavType.StringType })
            ) { backStackEntry ->
                val phone = backStackEntry.arguments?.getString("phone") ?: ""
                ResetPasswordScreen(phone = phone, onNavigate = { navController.navigate(it) })
            }

            // Location
            composable(ScreenRoute.LocationPermission.route) {
                LocationPermissionScreen(onNavigate = { navController.navigate(it) })
            }
            composable(ScreenRoute.SelectLocation.route) {
                SelectLocationScreen(onNavigate = { navController.navigate(it) })
            }
            composable(ScreenRoute.MapPicker.route) {
                MapPickerScreen(onNavigate = { navController.navigate(it) })
            }
            composable(ScreenRoute.AddressSearch.route) {
                AddressSearchScreen(onNavigate = { navController.navigate(it) })
            }
            composable(
                route = ScreenRoute.AddressConfirmation.route,
                arguments = listOf(
                    navArgument("lat") { type = NavType.FloatType },
                    navArgument("lng") { type = NavType.FloatType }
                )
            ) { backStackEntry ->
                val lat = backStackEntry.arguments?.getFloat("lat")?.toDouble() ?: 0.0
                val lng = backStackEntry.arguments?.getFloat("lng")?.toDouble() ?: 0.0
                AddressConfirmationScreen(lat = lat, lng = lng, onNavigate = { navController.navigate(it) })
            }

            // Main Hub
            composable(ScreenRoute.Home.route) {
                HomeScreen(onNavigate = { navController.navigate(it) })
            }
            composable(ScreenRoute.Categories.route) {
                CategoriesScreen(onNavigate = { navController.navigate(it) })
            }
            composable(
                route = ScreenRoute.CategoryProducts.route,
                arguments = listOf(
                    navArgument("categoryId") { type = NavType.StringType },
                    navArgument("categoryName") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val categoryId = backStackEntry.arguments?.getString("categoryId") ?: ""
                val categoryName = backStackEntry.arguments?.getString("categoryName") ?: ""
                CategoryProductsScreen(categoryId = categoryId, categoryName = categoryName, onNavigate = { navController.navigate(it) })
            }
            composable(ScreenRoute.Wishlist.route) {
                WishlistScreen(onNavigate = { navController.navigate(it) })
            }
            composable(ScreenRoute.Cart.route) {
                CartScreen(onNavigate = { navController.navigate(it) })
            }
            composable(ScreenRoute.Profile.route) {
                ProfileScreen(onNavigate = { navController.navigate(it) })
            }

            // Search
            composable(ScreenRoute.Search.route) {
                SearchScreen(onNavigate = { navController.navigate(it) })
            }
            composable(
                route = ScreenRoute.SearchResults.route,
                arguments = listOf(navArgument("query") { type = NavType.StringType })
            ) { backStackEntry ->
                val query = backStackEntry.arguments?.getString("query") ?: ""
                SearchResultsScreen(query = query, onNavigate = { navController.navigate(it) })
            }
            composable(ScreenRoute.Filters.route) {
                FiltersScreen(onNavigateBack = { navController.popBackStack() })
            }

            // Product
            composable(
                route = ScreenRoute.ProductDetails.route,
                arguments = listOf(navArgument("productId") { type = NavType.StringType })
            ) { backStackEntry ->
                val productId = backStackEntry.arguments?.getString("productId") ?: ""
                ProductDetailsScreen(productId = productId, onNavigate = { navController.navigate(it) })
            }
            composable(
                route = ScreenRoute.ProductReviews.route,
                arguments = listOf(navArgument("productId") { type = NavType.StringType })
            ) { backStackEntry ->
                val productId = backStackEntry.arguments?.getString("productId") ?: ""
                ProductReviewsScreen(productId = productId)
            }

            // Address & Cart/Checkout
            composable(ScreenRoute.SelectAddress.route) {
                SelectAddressScreen(onNavigate = { navController.navigate(it) })
            }
            composable(ScreenRoute.AddAddress.route) {
                AddEditAddressScreen(onNavigateBack = { navController.popBackStack() })
            }
            composable(ScreenRoute.SavedAddresses.route) {
                SavedAddressesScreen(onNavigate = { navController.navigate(it) })
            }
            composable(ScreenRoute.DeliveryOptions.route) {
                DeliveryOptionsScreen(onNavigate = { navController.navigate(it) })
            }
            composable(ScreenRoute.Checkout.route) {
                CheckoutScreen(onNavigate = { navController.navigate(it) })
            }
            composable(
                route = ScreenRoute.Payment.route,
                arguments = listOf(
                    navArgument("orderId") { type = NavType.StringType },
                    navArgument("amount") { type = NavType.FloatType }
                )
            ) { backStackEntry ->
                val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
                val amount = backStackEntry.arguments?.getFloat("amount")?.toDouble() ?: 0.0
                PaymentScreen(orderId = orderId, amount = amount, onNavigate = { navController.navigate(it) })
            }
            composable(
                route = ScreenRoute.PaymentProcessing.route,
                arguments = listOf(navArgument("orderId") { type = NavType.StringType })
            ) { backStackEntry ->
                val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
                PaymentProcessingScreen(orderId = orderId, onNavigate = { navController.navigate(it) })
            }
            composable(
                route = ScreenRoute.PaymentSuccess.route,
                arguments = listOf(navArgument("orderId") { type = NavType.StringType })
            ) { backStackEntry ->
                val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
                PaymentSuccessScreen(orderId = orderId, onNavigate = { navController.navigate(it) })
            }
            composable(
                route = ScreenRoute.PaymentFailed.route,
                arguments = listOf(navArgument("orderId") { type = NavType.StringType })
            ) { backStackEntry ->
                val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
                PaymentFailedScreen(orderId = orderId, onNavigate = { navController.navigate(it) })
            }
            composable(
                route = ScreenRoute.OrderConfirmation.route,
                arguments = listOf(navArgument("orderId") { type = NavType.StringType })
            ) { backStackEntry ->
                val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
                OrderConfirmationScreen(orderId = orderId, onNavigate = { navController.navigate(it) })
            }

            // Orders
            composable(ScreenRoute.Orders.route) {
                OrdersScreen(onNavigate = { navController.navigate(it) })
            }
            composable(
                route = ScreenRoute.OrderDetails.route,
                arguments = listOf(navArgument("orderId") { type = NavType.StringType })
            ) { backStackEntry ->
                val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
                OrderDetailsScreen(orderId = orderId, onNavigate = { navController.navigate(it) })
            }
            composable(
                route = ScreenRoute.LiveTracking.route,
                arguments = listOf(navArgument("orderId") { type = NavType.StringType })
            ) { backStackEntry ->
                val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
                TrackOrderScreen(orderId = orderId, onNavigate = { navController.navigate(it) })
            }
            composable(
                route = ScreenRoute.CancelOrder.route,
                arguments = listOf(navArgument("orderId") { type = NavType.StringType })
            ) { backStackEntry ->
                val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
                CancelOrderScreen(orderId = orderId, onNavigate = { navController.navigate(it) })
            }
            composable(
                route = ScreenRoute.RefundStatus.route,
                arguments = listOf(navArgument("orderId") { type = NavType.StringType })
            ) { backStackEntry ->
                val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
                RefundStatusScreen(orderId = orderId, onNavigate = { navController.navigate(it) })
            }
            composable(
                route = ScreenRoute.Invoice.route,
                arguments = listOf(navArgument("orderId") { type = NavType.StringType })
            ) { backStackEntry ->
                val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
                InvoiceScreen(orderId = orderId)
            }

            // Account & Support
            composable(ScreenRoute.EditProfile.route) {
                EditProfileScreen(onNavigateBack = { navController.popBackStack() })
            }
            composable(ScreenRoute.Notifications.route) {
                NotificationsScreen(onNavigate = { navController.navigate(it) })
            }
            composable(ScreenRoute.Coupons.route) {
                CouponsScreen(onNavigateBack = { navController.popBackStack() })
            }
            composable(ScreenRoute.PaymentMethods.route) {
                PaymentMethodsScreen()
            }
            composable(ScreenRoute.Settings.route) {
                SettingsScreen(onNavigate = { navController.navigate(it) })
            }
            composable(ScreenRoute.HelpSupport.route) {
                HelpSupportScreen(onNavigate = { navController.navigate(it) })
            }
            composable(ScreenRoute.FAQ.route) {
                FAQScreen()
            }
            composable(ScreenRoute.ContactSupport.route) {
                ContactSupportScreen()
            }
            composable(ScreenRoute.SupportTicket.route) {
                SupportTicketScreen(onNavigateBack = { navController.popBackStack() })
            }
            composable(ScreenRoute.PrivacyPolicy.route) {
                PrivacyPolicyScreen()
            }
            composable(ScreenRoute.TermsConditions.route) {
                TermsConditionsScreen()
            }
            composable(ScreenRoute.About.route) {
                AboutScreen()
            }
        }
    }
}
