package com.example.ezroom.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ezroom.core.Try
import com.example.ezroom.domain.model.Conversation
import com.example.ezroom.domain.model.Message
import com.example.ezroom.domain.usecase.GetConversationsUseCase
import com.example.ezroom.domain.usecase.GetMessagesUseCase
import com.example.ezroom.domain.usecase.SendMessageUseCase
import com.example.ezroom.ui.renter.discovery.toMessage
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ChatListUiState(
    val conversations: List<Conversation> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

data class ChatRoomUiState(
    val messages: List<Message> = emptyList(),
    val isLoading: Boolean = false,
    val otherPartyName: String = "",
    val error: String? = null
)

class ChatViewModel(
    private val getConversations: GetConversationsUseCase,
    private val getMessages: GetMessagesUseCase,
    private val sendMessage: SendMessageUseCase
) : ViewModel() {

    private val _listState = MutableStateFlow(ChatListUiState())
    val listState: StateFlow<ChatListUiState> = _listState.asStateFlow()

    private val _roomState = MutableStateFlow(ChatRoomUiState())
    val roomState: StateFlow<ChatRoomUiState> = _roomState.asStateFlow()

    init {
        loadConversations()
    }

    fun loadConversations() {
        viewModelScope.launch {
            _listState.update { it.copy(isLoading = true, error = null) }
            getConversations()
                .onEach { result ->
                    when (result) {
                        is Try.Success -> _listState.update { it.copy(conversations = result.value, isLoading = false) }
                        is Try.Failure -> _listState.update { it.copy(error = result.error.toMessage(), isLoading = false) }
                    }
                }
                .collect()
        }
    }

    fun loadMessages(conversationId: String, otherPartyName: String) {
        viewModelScope.launch {
            _roomState.update { it.copy(isLoading = true, otherPartyName = otherPartyName, error = null) }
            getMessages(conversationId)
                .onEach { result ->
                    when (result) {
                        is Try.Success -> _roomState.update { it.copy(messages = result.value, isLoading = false) }
                        is Try.Failure -> _roomState.update { it.copy(error = result.error.toMessage(), isLoading = false) }
                    }
                }
                .collect()
        }
    }

    fun onSendMessage(conversationId: String, text: String) {
        viewModelScope.launch {
            sendMessage(conversationId, text)
            loadMessages(conversationId, _roomState.value.otherPartyName)
            loadConversations()
        }
    }
}
