package com.housieshopping.app.di

import android.content.Context
import androidx.room.Room
import com.housieshopping.app.data.local.HousieDatabase
import com.housieshopping.app.data.local.dao.AddressDao
import com.housieshopping.app.data.local.dao.CartDao
import com.housieshopping.app.data.local.dao.OrderDao
import com.housieshopping.app.data.local.dao.ProductDao
import com.housieshopping.app.data.local.dao.WishlistDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): HousieDatabase {
        return Room.databaseBuilder(
            context,
            HousieDatabase::class.java,
            "housie_shopping.db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideProductDao(db: HousieDatabase): ProductDao = db.productDao()

    @Provides
    fun provideCartDao(db: HousieDatabase): CartDao = db.cartDao()

    @Provides
    fun provideWishlistDao(db: HousieDatabase): WishlistDao = db.wishlistDao()

    @Provides
    fun provideAddressDao(db: HousieDatabase): AddressDao = db.addressDao()

    @Provides
    fun provideOrderDao(db: HousieDatabase): OrderDao = db.orderDao()
}
