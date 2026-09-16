package com.housieshopping.app.di

import com.housieshopping.app.data.repository.AddressRepositoryImpl
import com.housieshopping.app.data.repository.AuthRepositoryImpl
import com.housieshopping.app.data.repository.CartRepositoryImpl
import com.housieshopping.app.data.repository.CouponRepositoryImpl
import com.housieshopping.app.data.repository.NotificationRepositoryImpl
import com.housieshopping.app.data.repository.OrderRepositoryImpl
import com.housieshopping.app.data.repository.PaymentRepositoryImpl
import com.housieshopping.app.data.repository.ProductRepositoryImpl
import com.housieshopping.app.data.repository.ProfileRepositoryImpl
import com.housieshopping.app.data.repository.SupportRepositoryImpl
import com.housieshopping.app.data.repository.WishlistRepositoryImpl
import com.housieshopping.app.domain.repository.AddressRepository
import com.housieshopping.app.domain.repository.AuthRepository
import com.housieshopping.app.domain.repository.CartRepository
import com.housieshopping.app.domain.repository.CouponRepository
import com.housieshopping.app.domain.repository.NotificationRepository
import com.housieshopping.app.domain.repository.OrderRepository
import com.housieshopping.app.domain.repository.PaymentRepository
import com.housieshopping.app.domain.repository.ProductRepository
import com.housieshopping.app.domain.repository.ProfileRepository
import com.housieshopping.app.domain.repository.SupportRepository
import com.housieshopping.app.domain.repository.WishlistRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindProductRepository(impl: ProductRepositoryImpl): ProductRepository

    @Binds
    @Singleton
    abstract fun bindCartRepository(impl: CartRepositoryImpl): CartRepository

    @Binds
    @Singleton
    abstract fun bindWishlistRepository(impl: WishlistRepositoryImpl): WishlistRepository

    @Binds
    @Singleton
    abstract fun bindOrderRepository(impl: OrderRepositoryImpl): OrderRepository

    @Binds
    @Singleton
    abstract fun bindAddressRepository(impl: AddressRepositoryImpl): AddressRepository

    @Binds
    @Singleton
    abstract fun bindCouponRepository(impl: CouponRepositoryImpl): CouponRepository

    @Binds
    @Singleton
    abstract fun bindProfileRepository(impl: ProfileRepositoryImpl): ProfileRepository

    @Binds
    @Singleton
    abstract fun bindPaymentRepository(impl: PaymentRepositoryImpl): PaymentRepository

    @Binds
    @Singleton
    abstract fun bindSupportRepository(impl: SupportRepositoryImpl): SupportRepository

    @Binds
    @Singleton
    abstract fun bindNotificationRepository(impl: NotificationRepositoryImpl): NotificationRepository
}
