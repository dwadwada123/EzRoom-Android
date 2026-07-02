package com.example.ezroom.domain.repository

import com.example.ezroom.domain.model.NotificationItem
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {
    fun getNotifications(): Flow<List<NotificationItem>>
    suspend fun markAsRead(notificationId: String)
    suspend fun markAllAsRead()
}
