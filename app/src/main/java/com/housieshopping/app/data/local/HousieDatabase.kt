package com.housieshopping.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.housieshopping.app.data.local.dao.AddressDao
import com.housieshopping.app.data.local.dao.CartDao
import com.housieshopping.app.data.local.dao.OrderDao
import com.housieshopping.app.data.local.dao.ProductDao
import com.housieshopping.app.data.local.dao.WishlistDao
import com.housieshopping.app.data.local.entity.AddressEntity
import com.housieshopping.app.data.local.entity.CartEntity
import com.housieshopping.app.data.local.entity.OrderEntity
import com.housieshopping.app.data.local.entity.ProductEntity
import com.housieshopping.app.data.local.entity.WishlistEntity

@Database(
    entities = [
        ProductEntity::class,
        CartEntity::class,
        WishlistEntity::class,
        AddressEntity::class,
        OrderEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class HousieDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun cartDao(): CartDao
    abstract fun wishlistDao(): WishlistDao
    abstract fun addressDao(): AddressDao
    abstract fun orderDao(): OrderDao
}
