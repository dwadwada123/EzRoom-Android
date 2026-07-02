package com.example.ezroom.data.repository

import com.example.ezroom.data.model.MockData
import com.example.ezroom.domain.model.Conversation
import com.example.ezroom.domain.model.Message
import com.example.ezroom.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.*

class ChatRepositoryImpl : ChatRepository {
    override fun getConversations(): Flow<List<Conversation>> = flow {
        emit(MockData.conversations)
    }

    override fun getMessages(conversationId: String): Flow<List<Message>> = flow {
        // In mock, we return the same list for any conversation
        emit(MockData.messages)
    }

    override suspend fun sendMessage(conversationId: String, text: String) {
        val newMessage = Message(
            id = UUID.randomUUID().toString(),
            senderId = "me",
            text = text,
            timestamp = System.currentTimeMillis(),
            isFromMe = true
        )
        MockData.messages.add(newMessage)
        
        // Update conversation last message
        val index = MockData.conversations.indexOfFirst { it.id == conversationId }
        if (index != -1) {
            MockData.conversations[index] = MockData.conversations[index].copy(
                lastMessage = text,
                timestamp = "Bây giờ"
            )
        }
    }
}
