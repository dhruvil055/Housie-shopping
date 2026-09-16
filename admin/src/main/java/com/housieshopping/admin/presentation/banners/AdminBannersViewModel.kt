package com.housieshopping.admin.presentation.banners

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.housieshopping.admin.data.repository.AdminRepository
import com.housieshopping.admin.domain.model.AdminBanner
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminBannersViewModel @Inject constructor(
    private val adminRepository: AdminRepository
) : ViewModel() {

    val banners: StateFlow<List<AdminBanner>> = adminRepository.getAllBanners()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun saveBanner(title: String, subtitle: String, imageUrl: String) {
        viewModelScope.launch {
            val newBanner = AdminBanner(
                id = "",
                title = title,
                subtitle = subtitle,
                imageUrl = imageUrl.ifEmpty { "https://images.unsplash.com/photo-1541888946425-d0fbb186a5b2?w=800" },
                categoryTarget = "cat1",
                isActive = true
            )
            adminRepository.saveBanner(newBanner)
        }
    }

    fun deleteBanner(bannerId: String) {
        viewModelScope.launch {
            adminRepository.deleteBanner(bannerId)
        }
    }
}
