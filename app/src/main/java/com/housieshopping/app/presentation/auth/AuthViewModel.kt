package com.housieshopping.app.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.housieshopping.app.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false,
    val otpSent: Boolean = false
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState

    fun login(emailOrPhone: String, password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true)
            val result = authRepository.login(emailOrPhone, password)
            result.onSuccess {
                _uiState.value = AuthUiState(isSuccess = true)
                onSuccess()
            }.onFailure {
                _uiState.value = AuthUiState(error = it.message)
            }
        }
    }

    fun register(name: String, email: String, phone: String, password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true)
            val result = authRepository.register(name, email, phone, password)
            result.onSuccess {
                _uiState.value = AuthUiState(isSuccess = true)
                onSuccess()
            }.onFailure {
                _uiState.value = AuthUiState(error = it.message)
            }
        }
    }

    fun verifyOtp(phone: String, otp: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true)
            val result = authRepository.verifyOtp(phone, otp)
            result.onSuccess {
                _uiState.value = AuthUiState(isSuccess = true)
                onSuccess()
            }.onFailure {
                _uiState.value = AuthUiState(error = it.message)
            }
        }
    }

    fun forgotPassword(emailOrPhone: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true)
            val result = authRepository.forgotPassword(emailOrPhone)
            result.onSuccess {
                _uiState.value = AuthUiState(otpSent = true)
                onSuccess()
            }.onFailure {
                _uiState.value = AuthUiState(error = it.message)
            }
        }
    }

    fun resetPassword(phone: String, otp: String, newPassword: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true)
            val result = authRepository.resetPassword(phone, otp, newPassword)
            result.onSuccess {
                _uiState.value = AuthUiState(isSuccess = true)
                onSuccess()
            }.onFailure {
                _uiState.value = AuthUiState(error = it.message)
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
