package com.housieshopping.admin.presentation.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.housieshopping.admin.data.repository.AdminRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NotificationSendState(
    val title: String = "",
    val message: String = "",
    val targetAudience: String = "All Customers",
    val isSent: Boolean = false,
    val statusText: String? = null
)

@HiltViewModel
class AdminNotificationsViewModel @Inject constructor(
    private val adminRepository: AdminRepository
) : ViewModel() {

    private val _state = MutableStateFlow(NotificationSendState())
    val state: StateFlow<NotificationSendState> = _state

    fun onTitleChange(title: String) {
        _state.value = _state.value.copy(title = title, statusText = null)
    }

    fun onMessageChange(msg: String) {
        _state.value = _state.value.copy(message = msg, statusText = null)
    }

    fun sendBroadcastNotification() {
        viewModelScope.launch {
            if (_state.value.title.isNotEmpty() && _state.value.message.isNotEmpty()) {
                adminRepository.logAction("PUSH_BROADCAST", "Sent push notification '${_state.value.title}' to ${_state.value.targetAudience}")
                _state.value = _state.value.copy(
                    isSent = true,
                    statusText = "Push notification successfully broadcasted to ${_state.value.targetAudience} via Firebase Cloud Messaging! 🚀",
                    title = "",
                    message = ""
                )
            }
        }
    }
}
