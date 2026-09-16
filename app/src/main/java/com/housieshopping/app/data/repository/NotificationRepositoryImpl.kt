package com.housieshopping.app.data.repository

import com.housieshopping.app.data.mock.MockData
import com.housieshopping.app.domain.model.NotificationItem
import com.housieshopping.app.domain.repository.NotificationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRepositoryImpl @Inject constructor() : NotificationRepository {

    private val notificationsFlow = MutableStateFlow<List<NotificationItem>>(MockData.mockNotifications)

    override fun getNotifications(): Flow<List<NotificationItem>> = notificationsFlow

    override suspend fun markAsRead(id: String): Result<Unit> {
        val current = notificationsFlow.value.map {
            if (it.id == id) it.copy(isRead = true) else it
        }
        notificationsFlow.value = current
        return Result.success(Unit)
    }

    override suspend fun clearAll(): Result<Unit> {
        notificationsFlow.value = emptyList()
        return Result.success(Unit)
    }
}
