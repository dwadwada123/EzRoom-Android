package com.example.ezroom.data.repository

import com.example.ezroom.domain.model.NotificationItem
import com.example.ezroom.domain.repository.NotificationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class NotificationRepositoryImpl(
    private val api: com.example.ezroom.data.remote.NotificationApi = com.example.ezroom.data.remote.NetworkClient.createService()
) : NotificationRepository {
    override fun getNotifications(): Flow<List<NotificationItem>> = flow {
        try {
            val user = com.example.ezroom.util.TokenManager.getUser()
            if (user != null) {
                emit(api.getNotifications(user.id))
            } else {
                emit(emptyList())
            }
        } catch (e: java.lang.Exception) {
            emit(emptyList())
        }
    }

    override suspend fun markAsRead(notificationId: String) {
        try {
            api.markAsRead(notificationId)
        } catch (e: java.lang.Exception) {
            // Error handling
        }
    }

    override suspend fun markAllAsRead() {
        try {
            val user = com.example.ezroom.util.TokenManager.getUser() ?: return
            api.markAllAsRead(mapOf("userId" to user.id))
        } catch (e: java.lang.Exception) {
            // Error handling
        }
    }
}
