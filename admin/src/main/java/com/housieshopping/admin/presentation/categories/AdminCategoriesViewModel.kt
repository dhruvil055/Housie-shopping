package com.housieshopping.admin.presentation.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.housieshopping.admin.data.repository.AdminRepository
import com.housieshopping.admin.domain.model.AdminCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminCategoriesViewModel @Inject constructor(
    private val adminRepository: AdminRepository
) : ViewModel() {

    val categories: StateFlow<List<AdminCategory>> = adminRepository.getAllCategories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun saveCategory(name: String, description: String) {
        viewModelScope.launch {
            val newCategory = AdminCategory(
                id = "",
                name = name,
                description = description,
                iconUrl = "https://images.unsplash.com/photo-1581094794329-c8112a89af12?w=500",
                productCount = 0,
                isActive = true
            )
            adminRepository.saveCategory(newCategory)
        }
    }
}
