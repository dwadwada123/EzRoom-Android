package com.example.ezroom.domain.usecase

import com.example.ezroom.core.AppError
import com.example.ezroom.core.Try
import com.example.ezroom.domain.model.Conversation
import com.example.ezroom.domain.model.Message
import com.example.ezroom.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class GetConversationsUseCase(private val repository: ChatRepository) {
    operator fun invoke(): Flow<Try<List<Conversation>>> = repository.getConversations()
        .map<List<Conversation>, Try<List<Conversation>>> { Try.Success(it) }
        .catch { emit(Try.Failure(AppError.Unknown(it))) }
}

class GetMessagesUseCase(private val repository: ChatRepository) {
    operator fun invoke(conversationId: String): Flow<Try<List<Message>>> = repository.getMessages(conversationId)
        .map<List<Message>, Try<List<Message>>> { Try.Success(it) }
        .catch { emit(Try.Failure(AppError.Unknown(it))) }
}

class SendMessageUseCase(private val repository: ChatRepository) {
    suspend operator fun invoke(conversationId: String, text: String, imageUrl: String? = null, lat: Double? = null, lng: Double? = null) = 
        repository.sendMessage(conversationId, text, imageUrl, lat, lng)
}

class UploadImageUseCase(private val repository: ChatRepository) {
    suspend operator fun invoke(fileBytes: ByteArray, fileName: String, mimeType: String): String? = 
        repository.uploadImage(fileBytes, fileName, mimeType)
}
