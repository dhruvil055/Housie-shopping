package com.housieshopping.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.housieshopping.app.data.local.entity.CartEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {
    @Query("SELECT * FROM cart")
    fun getCartItems(): Flow<List<CartEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(cartEntity: CartEntity)

    @Query("DELETE FROM cart WHERE id = :id")
    suspend fun deleteCartItem(id: String)

    @Query("DELETE FROM cart")
    suspend fun clearCart()
}
