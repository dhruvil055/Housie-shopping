package com.housieshopping.app.data.repository

import com.housieshopping.app.data.mock.MockData
import com.housieshopping.app.domain.model.Address
import com.housieshopping.app.domain.repository.AddressRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AddressRepositoryImpl @Inject constructor() : AddressRepository {

    private val addressesFlow = MutableStateFlow<List<Address>>(MockData.savedAddresses)

    override fun getAddresses(): Flow<List<Address>> = addressesFlow

    override fun getDefaultAddress(): Flow<Address?> {
        return addressesFlow.map { list -> list.find { it.isDefault } ?: list.firstOrNull() }
    }

    override suspend fun getAddressById(id: String): Result<Address> {
        val address = addressesFlow.value.find { it.id == id } ?: addressesFlow.value.first()
        return Result.success(address)
    }

    override suspend fun addAddress(address: Address): Result<Address> {
        val current = addressesFlow.value.toMutableList()
        val newAddress = if (address.id.isBlank()) address.copy(id = "addr_${System.currentTimeMillis()}") else address
        if (newAddress.isDefault) {
            current.indices.forEach { current[it] = current[it].copy(isDefault = false) }
        }
        current.add(newAddress)
        addressesFlow.value = current
        return Result.success(newAddress)
    }

    override suspend fun updateAddress(address: Address): Result<Address> {
        val current = addressesFlow.value.toMutableList()
        val index = current.indexOfFirst { it.id == address.id }
        if (index >= 0) {
            if (address.isDefault) {
                current.indices.forEach { current[it] = current[it].copy(isDefault = false) }
            }
            current[index] = address
            addressesFlow.value = current
        }
        return Result.success(address)
    }

    override suspend fun deleteAddress(id: String): Result<Boolean> {
        addressesFlow.value = addressesFlow.value.filter { it.id != id }
        return Result.success(true)
    }

    override suspend fun setDefaultAddress(id: String): Result<Boolean> {
        val current = addressesFlow.value.map { it.copy(isDefault = it.id == id) }
        addressesFlow.value = current
        return Result.success(true)
    }

    override suspend fun checkServiceability(pinCode: String): Result<Boolean> {
        if (pinCode.length != 6) return Result.failure(Exception("Enter a valid 6-digit PIN code."))
        return Result.success(true)
    }
}
