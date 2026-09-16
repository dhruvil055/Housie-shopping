package com.housieshopping.admin.presentation.support

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.housieshopping.admin.data.repository.AdminRepository
import com.housieshopping.admin.domain.model.AdminSupportTicket
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminSupportViewModel @Inject constructor(
    private val adminRepository: AdminRepository
) : ViewModel() {

    val tickets: StateFlow<List<AdminSupportTicket>> = adminRepository.getAllSupportTickets()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun resolveTicket(ticketId: String, notes: String) {
        viewModelScope.launch {
            adminRepository.updateTicket(ticketId, "Resolved", notes)
        }
    }
}
