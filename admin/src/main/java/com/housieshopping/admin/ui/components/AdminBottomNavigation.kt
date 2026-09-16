package com.housieshopping.admin.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.housieshopping.admin.navigation.AdminScreenRoute
import com.housieshopping.admin.ui.theme.AmberAccent
import com.housieshopping.admin.ui.theme.NavyPrimary

data class AdminBottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
)

@Composable
fun AdminBottomNavigation(
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    val items = listOf(
        AdminBottomNavItem(AdminScreenRoute.Dashboard.route, "Dashboard", Icons.Default.Dashboard),
        AdminBottomNavItem(AdminScreenRoute.Products.route, "Inventory", Icons.Default.Inventory),
        AdminBottomNavItem(AdminScreenRoute.Orders.route, "Orders", Icons.Default.LocalShipping),
        AdminBottomNavItem(AdminScreenRoute.Customers.route, "Customers", Icons.Default.People),
        AdminBottomNavItem(AdminScreenRoute.Analytics.route, "Analytics", Icons.Default.Analytics)
    )

    Surface(
        tonalElevation = 8.dp,
        shadowElevation = 8.dp,
        color = NavyPrimary
    ) {
        NavigationBar(
            containerColor = NavyPrimary,
            contentColor = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
        ) {
            items.forEach { item ->
                val isSelected = currentRoute == item.route
                NavigationBarItem(
                    selected = isSelected,
                    onClick = { onNavigate(item.route) },
                    alwaysShowLabel = true,
                    icon = {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.title,
                            modifier = Modifier.size(22.dp)
                        )
                    },
                    label = {
                        Text(
                            text = item.title,
                            fontSize = 10.5.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = AmberAccent,
                        selectedTextColor = AmberAccent,
                        unselectedIconColor = Color.White.copy(alpha = 0.6f),
                        unselectedTextColor = Color.White.copy(alpha = 0.6f),
                        indicatorColor = Color.White.copy(alpha = 0.1f)
                    )
                )
            }
        }
    }
}
