package com.housieshopping.admin.di

import android.content.Context
import androidx.room.Room
import com.housieshopping.admin.BuildConfig
import com.housieshopping.admin.core.network.AdminApiService
import com.housieshopping.admin.core.network.AdminAuthInterceptor
import com.housieshopping.admin.data.database.AdminDao
import com.housieshopping.admin.data.database.AdminDatabase
import com.housieshopping.admin.data.repository.AdminRepository
import com.housieshopping.admin.data.repository.AdminRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AdminAppModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(
        adminAuthInterceptor: AdminAuthInterceptor
    ): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
        }
        return OkHttpClient.Builder()
            .addInterceptor(adminAuthInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideAdminApiService(okHttpClient: OkHttpClient): AdminApiService {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AdminApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideAdminDatabase(
        @ApplicationContext context: Context
    ): AdminDatabase {
        return Room.databaseBuilder(
            context,
            AdminDatabase::class.java,
            "housie_admin_db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideAdminDao(database: AdminDatabase): AdminDao {
        return database.adminDao()
    }

    @Provides
    @Singleton
    fun provideAdminRepository(
        adminDao: AdminDao,
        adminApiService: AdminApiService
    ): AdminRepository {
        return AdminRepositoryImpl(adminDao, adminApiService)
    }
}

