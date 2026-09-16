package com.housieshopping.app.presentation.support

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.housieshopping.app.domain.model.FAQItem
import com.housieshopping.app.domain.model.SupportTicket
import com.housieshopping.app.domain.repository.SupportRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SupportUiState(
    val faqs: List<FAQItem> = emptyList(),
    val tickets: List<SupportTicket> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val isSubmitted: Boolean = false
)

@HiltViewModel
class SupportViewModel @Inject constructor(
    private val supportRepository: SupportRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SupportUiState())
    val uiState: StateFlow<SupportUiState> = _uiState

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            val faqs = supportRepository.getFAQs().getOrDefault(emptyList())
            val tickets = supportRepository.getSupportTickets().getOrDefault(emptyList())
            _uiState.value = _uiState.value.copy(faqs = faqs, tickets = tickets)
        }
    }

    fun createTicket(subject: String, category: String, description: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            supportRepository.createSupportTicket(subject, category, description)
            loadData()
            onSuccess()
        }
    }
}
