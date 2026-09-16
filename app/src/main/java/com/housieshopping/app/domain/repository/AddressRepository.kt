package com.housieshopping.app.domain.repository

import com.housieshopping.app.domain.model.Address
import kotlinx.coroutines.flow.Flow

interface AddressRepository {
    fun getAddresses(): Flow<List<Address>>
    fun getDefaultAddress(): Flow<Address?>
    suspend fun getAddressById(id: String): Result<Address>
    suspend fun addAddress(address: Address): Result<Address>
    suspend fun updateAddress(address: Address): Result<Address>
    suspend fun deleteAddress(id: String): Result<Boolean>
    suspend fun setDefaultAddress(id: String): Result<Boolean>
    suspend fun checkServiceability(pinCode: String): Result<Boolean>
}
