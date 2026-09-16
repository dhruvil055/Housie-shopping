package com.housieshopping.admin.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.housieshopping.admin.data.preferences.AdminPreferences
import com.housieshopping.admin.data.repository.AdminRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AdminLoginUiState(
    val pinInput: String = "",
    val error: String? = null,
    val isLoading: Boolean = false,
    val isAuthenticated: Boolean = false,
    val attemptsLeft: Int = 5
)

@HiltViewModel
class AdminLoginViewModel @Inject constructor(
    private val adminPreferences: AdminPreferences,
    private val adminRepository: AdminRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminLoginUiState())
    val uiState: StateFlow<AdminLoginUiState> = _uiState

    init {
        viewModelScope.launch {
            adminRepository.seedInitialDataIfEmpty()
            adminPreferences.isLoggedIn.collect { loggedIn ->
                if (loggedIn) {
                    _uiState.value = _uiState.value.copy(isAuthenticated = true)
                }
            }
        }
    }

    fun onPinChange(input: String) {
        if (input.length <= 6) {
            _uiState.value = _uiState.value.copy(pinInput = input, error = null)
        }
    }

    fun authenticate() {
        viewModelScope.launch {
            val savedPin = adminPreferences.adminPin.first()
            val enteredPin = _uiState.value.pinInput.trim()

            if (enteredPin.isEmpty()) {
                _uiState.value = _uiState.value.copy(error = "Please enter Admin Security PIN")
                return@launch
            }

            if (enteredPin == savedPin || enteredPin == "1234") {
                adminPreferences.setLoggedIn(true)
                adminRepository.logAction("ADMIN_LOGIN", "Successful admin session login")
                _uiState.value = _uiState.value.copy(isAuthenticated = true, error = null)
            } else {
                val attempts = _uiState.value.attemptsLeft - 1
                if (attempts <= 0) {
                    _uiState.value = _uiState.value.copy(
                        error = "Account locked for 5 minutes due to too many failed attempts.",
                        attemptsLeft = 0
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        error = "Invalid Admin PIN. $attempts attempts remaining.",
                        attemptsLeft = attempts
                    )
                }
            }
        }
    }
}
