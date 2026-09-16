package com.housieshopping.app.presentation.address

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.housieshopping.app.domain.model.Address
import com.housieshopping.app.domain.model.AddressType
import com.housieshopping.app.domain.repository.AddressRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddressUiState(
    val addresses: List<Address> = emptyList(),
    val selectedAddressId: String? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class AddressViewModel @Inject constructor(
    private val addressRepository: AddressRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddressUiState())
    val uiState: StateFlow<AddressUiState> = _uiState

    init {
        loadAddresses()
    }

    fun loadAddresses() {
        viewModelScope.launch {
            addressRepository.getAddresses().collect { list ->
                _uiState.value = _uiState.value.copy(
                    addresses = list,
                    selectedAddressId = list.find { it.isDefault }?.id ?: list.firstOrNull()?.id
                )
            }
        }
    }

    fun selectAddress(addressId: String) {
        _uiState.value = _uiState.value.copy(selectedAddressId = addressId)
    }

    fun saveAddress(
        fullName: String,
        phone: String,
        houseFlat: String,
        street: String,
        area: String,
        city: String,
        state: String,
        pinCode: String,
        landmark: String?,
        type: AddressType,
        isDefault: Boolean,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            val address = Address(
                id = "addr_${System.currentTimeMillis()}",
                fullName = fullName,
                phone = phone,
                houseFlat = houseFlat,
                street = street,
                area = area,
                city = city,
                state = state,
                pinCode = pinCode,
                landmark = landmark,
                type = type,
                isDefault = isDefault
            )
            addressRepository.addAddress(address)
            onSuccess()
        }
    }

    fun deleteAddress(id: String) {
        viewModelScope.launch {
            addressRepository.deleteAddress(id)
        }
    }

    fun setDefault(id: String) {
        viewModelScope.launch {
            addressRepository.setDefaultAddress(id)
        }
    }
}
