package com.housieshopping.admin.presentation.reviews

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.housieshopping.admin.data.repository.AdminRepository
import com.housieshopping.admin.domain.model.AdminReview
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminReviewsViewModel @Inject constructor(
    private val adminRepository: AdminRepository
) : ViewModel() {

    val reviews: StateFlow<List<AdminReview>> = adminRepository.getAllReviews()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun deleteReview(reviewId: String) {
        viewModelScope.launch {
            adminRepository.deleteReview(reviewId)
        }
    }
}
