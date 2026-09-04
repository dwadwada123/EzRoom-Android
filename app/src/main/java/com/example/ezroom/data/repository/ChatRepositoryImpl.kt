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
    private fun generateObjectIdHex(): String {
        val chars = "0123456789abcdef"
        val timestampHex = (System.currentTimeMillis() / 1000).toString(16).padStart(8, '0')
        val randomHex = (1..16).map { chars.random() }.joinToString("")
        return (timestampHex + randomHex).take(24)
    }

    private fun getCanonicalConversationId(rawId: String, currentUserId: String): String {
        val trimmed = rawId.trim()
        if (trimmed.length == 24 && trimmed.all { it in "0123456789abcdefABCDEF" }) {
            return trimmed.lowercase()
        }
        val parts = trimmed.split("_").filter { it.isNotBlank() && it != "conv" }
        val key = if (parts.size >= 2) {
            listOf(parts[0], parts[1]).sorted().joinToString("_")
        } else if (currentUserId.isNotBlank() && parts.size == 1) {
            listOf(currentUserId, parts[0]).sorted().joinToString("_")
        } else {
            trimmed
        }
        val md = java.security.MessageDigest.getInstance("MD5")
        val bytes = md.digest(key.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }.take(24)
    }

    override fun getConversations(): Flow<List<Conversation>> = flow {
        try {
            val user = com.example.ezroom.util.TokenManager.getUser()
            if (user != null) {
                emit(api.getConversations(user.id))
            } else {
                emit(emptyList())
            }
        } catch (e: Exception) {
            android.util.Log.e("ChatRepository", "getConversations error", e)
            emit(emptyList())
        }
    }

    override fun getMessages(conversationId: String): Flow<List<Message>> = flow {
        try {
            val user = com.example.ezroom.util.TokenManager.getUser()
            if (user != null) {
                val canonicalConvId = getCanonicalConversationId(conversationId, user.id)
                emit(api.getMessages(canonicalConvId, user.id))
            } else {
                emit(emptyList())
            }
        } catch (e: Exception) {
            android.util.Log.e("ChatRepository", "getMessages error", e)
            emit(emptyList())
        }
    }

    override suspend fun sendMessage(conversationId: String, text: String, imageUrl: String?, lat: Double?, lng: Double?) {
        try {
            val user = com.example.ezroom.util.TokenManager.getUser() ?: return
            val canonicalConvId = getCanonicalConversationId(conversationId, user.id)

            // Extract renterId and hostId from conversationId (conv_{renterId}_{hostId} or {renterId}_{hostId})
            val parts = conversationId.split("_").filter { it.isNotBlank() && it != "conv" }
            val otherId = parts.firstOrNull { it != user.id } ?: ""
            val renterId = if (user.role == "RENTER") user.id else otherId
            val hostId = if (user.role == "HOST") user.id else otherId

            val msgContent = when {
                text.isNotBlank() -> text.trim()
                imageUrl != null -> "Đã gửi một ảnh"
                lat != null -> "Đã gửi vị trí"
                else -> "Tin nhắn"
            }

            val request = com.example.ezroom.data.remote.SendMessageRequest(
                id = generateObjectIdHex(),
                conversationId = canonicalConvId,
                senderId = user.id,
                content = msgContent,
                timestamp = System.currentTimeMillis().toString(),
                renterId = renterId.takeIf { it.isNotBlank() },
                hostId = hostId.takeIf { it.isNotBlank() },
                imageUrl = imageUrl,
                latitude = lat,
                longitude = lng
            )
            api.sendMessage(request)
        } catch (e: Exception) {
            android.util.Log.e("ChatRepository", "sendMessage error", e)
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
            android.util.Log.e("ChatRepository", "uploadImage error", e)
            null
        }
    }
}
