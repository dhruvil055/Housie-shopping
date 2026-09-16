package com.housieshopping.app.domain.repository

import com.housieshopping.app.domain.model.NotificationItem
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {
    fun getNotifications(): Flow<List<NotificationItem>>
    suspend fun markAsRead(id: String): Result<Unit>
    suspend fun clearAll(): Result<Unit>
}
