package com.example.ezroom.domain.repository

import com.example.ezroom.domain.model.Conversation
import com.example.ezroom.domain.model.Message
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun getConversations(): Flow<List<Conversation>>
    fun getMessages(conversationId: String): Flow<List<Message>>
    suspend fun sendMessage(conversationId: String, text: String)
}
