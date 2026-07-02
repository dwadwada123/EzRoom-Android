package com.example.ezroom.domain.usecase

import com.example.ezroom.core.AppError
import com.example.ezroom.core.Try
import com.example.ezroom.domain.model.NotificationItem
import com.example.ezroom.domain.repository.NotificationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class GetNotificationsUseCase(private val repository: NotificationRepository) {
    operator fun invoke(): Flow<Try<List<NotificationItem>>> = repository.getNotifications()
        .map<List<NotificationItem>, Try<List<NotificationItem>>> { Try.Success(it) }
        .catch { emit(Try.Failure(AppError.Unknown(it))) }
}

class MarkNotificationAsReadUseCase(private val repository: NotificationRepository) {
    suspend operator fun invoke(id: String) = repository.markAsRead(id)
}

class MarkAllNotificationsAsReadUseCase(private val repository: NotificationRepository) {
    suspend operator fun invoke() = repository.markAllAsRead()
}
