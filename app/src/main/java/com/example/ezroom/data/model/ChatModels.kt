package com.example.ezroom.data.model

data class Message(
    val id: String,
    val senderId: String,
    val text: String,
    val timestamp: Long,
    val isFromMe: Boolean
)

data class Conversation(
    val id: String,
    val otherPartyName: String,
    val lastMessage: String,
    val timestamp: String,
    val unreadCount: Int,
    val profileImage: String? = null
)
