package com.example.ezroom.data.repository

import com.example.ezroom.domain.model.Conversation
import com.example.ezroom.domain.model.Message
import com.example.ezroom.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody

class ChatRepositoryImpl(
    private val api: com.example.ezroom.data.remote.ChatApi = com.example.ezroom.data.remote.NetworkClient.createService()
) : ChatRepository {
    override fun getConversations(): Flow<List<Conversation>> = flow {
        try {
            val user = com.example.ezroom.util.TokenManager.getUser()
            if (user != null) {
                emit(api.getConversations(user.id))
            } else {
                emit(emptyList())
            }
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    override fun getMessages(conversationId: String): Flow<List<Message>> = flow {
        try {
            val user = com.example.ezroom.util.TokenManager.getUser()
            if (user != null) {
                emit(api.getMessages(conversationId, user.id))
            } else {
                emit(emptyList())
            }
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    override suspend fun sendMessage(conversationId: String, text: String, imageUrl: String?, lat: Double?, lng: Double?) {
        try {
            val user = com.example.ezroom.util.TokenManager.getUser() ?: return
            
            // Extract renterId and hostId from conversationId (conv_{renterId}_{hostId})
            val parts = conversationId.split("_")
            val renterId = if (parts.size == 3) parts[1] else if (user.role == "RENTER") user.id else ""
            val hostId = if (parts.size == 3) parts[2] else if (user.role == "HOST") user.id else ""

            val request = com.example.ezroom.data.remote.SendMessageRequest(
                conversationId = conversationId,
                senderId = user.id,
                content = text,
                timestamp = System.currentTimeMillis().toString(),
                renterId = renterId.takeIf { it.isNotBlank() },
                hostId = hostId.takeIf { it.isNotBlank() },
                imageUrl = imageUrl,
                latitude = lat,
                longitude = lng
            )
            api.sendMessage(request)
        } catch (e: Exception) {
            // Error handling
        }
    }

    override suspend fun uploadImage(fileBytes: ByteArray, fileName: String, mimeType: String): String? {
        return try {
            val mediaType = mimeType.toMediaTypeOrNull()
            val requestBody = fileBytes.toRequestBody(mediaType)
            val body = okhttp3.MultipartBody.Part.createFormData("image", fileName, requestBody)
            val response = api.uploadImage(body)
            if (response.success) response.url else null
        } catch (e: Exception) {
            null
        }
    }
}
