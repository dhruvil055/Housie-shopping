package com.housieshopping.app.presentation.location

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.housieshopping.app.data.mock.MockData
import com.housieshopping.app.domain.model.Address
import com.housieshopping.app.domain.repository.AddressRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LocationUiState(
    val selectedLocationName: String = "DLF Phase 2, Gurugram, Haryana - 122002",
    val lat: Double = 28.4595,
    val lng: Double = 77.0266,
    val isServiceable: Boolean = true,
    val savedAddresses: List<Address> = emptyList(),
    val searchQuery: String = "",
    val searchResults: List<String> = emptyList()
)

@HiltViewModel
class LocationViewModel @Inject constructor(
    private val addressRepository: AddressRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LocationUiState())
    val uiState: StateFlow<LocationUiState> = _uiState

    init {
        loadAddresses()
    }

    private fun loadAddresses() {
        viewModelScope.launch {
            addressRepository.getAddresses().collect { list ->
                _uiState.value = _uiState.value.copy(savedAddresses = list)
            }
        }
    }

    fun searchAddress(query: String) {
        _uiState.value = _uiState.value.copy(
            searchQuery = query,
            searchResults = if (query.isBlank()) emptyList() else listOf(
                "$query, Sector 14, Gurugram",
                "$query, MG Road, Bengaluru",
                "$query, Outer Ring Road, New Delhi"
            )
        )
    }

    fun selectLocation(name: String, lat: Double = 28.4595, lng: Double = 77.0266) {
        _uiState.value = _uiState.value.copy(
            selectedLocationName = name,
            lat = lat,
            lng = lng,
            isServiceable = true
        )
    }
}
