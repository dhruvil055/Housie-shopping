package com.housieshopping.admin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.housieshopping.admin.data.preferences.AdminPreferences
import com.housieshopping.admin.data.repository.AdminRepository
import com.housieshopping.admin.navigation.AdminNavGraph
import com.housieshopping.admin.ui.theme.HousieAdminTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AdminMainActivity : ComponentActivity() {

    @Inject
    lateinit var adminPreferences: AdminPreferences

    @Inject
    lateinit var adminRepository: AdminRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HousieAdminTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AdminNavGraph(
                        adminPreferences = adminPreferences,
                        adminRepository = adminRepository
                    )
                }
            }
        }
    }
}
