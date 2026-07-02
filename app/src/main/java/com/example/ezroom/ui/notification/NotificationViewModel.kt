package com.example.ezroom.ui.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ezroom.core.Try
import com.example.ezroom.domain.model.NotificationItem
import com.example.ezroom.domain.usecase.GetNotificationsUseCase
import com.example.ezroom.domain.usecase.MarkNotificationAsReadUseCase
import com.example.ezroom.domain.usecase.MarkAllNotificationsAsReadUseCase
import com.example.ezroom.ui.renter.discovery.toMessage
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class NotificationUiState(
    val notifications: List<NotificationItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class NotificationViewModel(
    private val getNotifications: GetNotificationsUseCase,
    private val markAsRead: MarkNotificationAsReadUseCase,
    private val markAllAsRead: MarkAllNotificationsAsReadUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationUiState())
    val uiState: StateFlow<NotificationUiState> = _uiState.asStateFlow()

    init {
        loadNotifications()
    }

    fun loadNotifications() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            getNotifications()
                .onEach { result ->
                    when (result) {
                        is Try.Success -> _uiState.update { it.copy(notifications = result.value, isLoading = false) }
                        is Try.Failure -> _uiState.update { it.copy(error = result.error.toMessage(), isLoading = false) }
                    }
                }
                .collect()
        }
    }

    fun onNotificationRead(id: String) {
        viewModelScope.launch {
            markAsRead(id)
            loadNotifications()
        }
    }

    fun onMarkAllAsRead() {
        viewModelScope.launch {
            markAllAsRead()
            loadNotifications()
        }
    }
}
