package com.housieshopping.admin.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.housieshopping.admin.data.preferences.AdminPreferences
import com.housieshopping.admin.data.repository.AdminRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminSettingsViewModel @Inject constructor(
    private val adminPreferences: AdminPreferences,
    private val adminRepository: AdminRepository
) : ViewModel() {

    val adminPin: StateFlow<String> = adminPreferences.adminPin
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "1234")

    val isDarkMode: StateFlow<Boolean> = adminPreferences.isDarkMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    fun changePin(newPin: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            adminPreferences.updateAdminPin(newPin)
            adminRepository.logAction("PIN_CHANGED", "Admin updated security PIN")
            onComplete()
        }
    }

    fun toggleDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            adminPreferences.setDarkMode(enabled)
        }
    }
}
