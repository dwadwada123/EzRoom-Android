package com.example.ezroom.data.repository

import com.example.ezroom.data.model.MockData
import com.example.ezroom.domain.model.NotificationItem
import com.example.ezroom.domain.repository.NotificationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class NotificationRepositoryImpl : NotificationRepository {
    override fun getNotifications(): Flow<List<NotificationItem>> = flow {
        emit(MockData.notifications)
    }

    override suspend fun markAsRead(notificationId: String) {
        val index = MockData.notifications.indexOfFirst { it.id == notificationId }
        if (index != -1) {
            MockData.notifications[index] = MockData.notifications[index].copy(isRead = true)
        }
    }

    override suspend fun markAllAsRead() {
        MockData.notifications.forEachIndexed { index, item ->
            if (!item.isRead) {
                MockData.notifications[index] = item.copy(isRead = true)
            }
        }
    }
}
