package com.example.ezroom.domain.model

data class Message(
    val id: String,
    val senderId: String,
    val text: String,
    val timestamp: Long,
    val isFromMe: Boolean,
    val imageUrl: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null
)

data class Conversation(
    val id: String,
    val otherPartyName: String? = null,
    val otherPartyPhone: String? = null,
    val lastMessage: String? = null,
    val timestamp: String? = null,
    val unreadCount: Int = 0,
    val profileImage: String? = null
)
