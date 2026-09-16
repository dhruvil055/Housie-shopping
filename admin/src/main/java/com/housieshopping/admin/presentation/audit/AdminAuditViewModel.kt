package com.housieshopping.admin.presentation.audit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.housieshopping.admin.data.repository.AdminRepository
import com.housieshopping.admin.domain.model.AuditLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class AdminAuditViewModel @Inject constructor(
    adminRepository: AdminRepository
) : ViewModel() {

    val auditLogs: StateFlow<List<AuditLog>> = adminRepository.getAllAuditLogs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
