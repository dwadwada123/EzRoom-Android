package com.example.ezroom.domain.model

data class User(
    val id: String,
    val name: String,
    val email: String,
    val phone: String,
    val avatarUrl: String? = null,
    val role: String, // RENTER or HOST
    val isEkycVerified: Boolean = false,
    val ekycStatus: String? = null,
    val ekycRejectReason: String? = null,
    val creditScore: Float = 0f,
    val favoriteRoomIds: List<String> = emptyList(),
    val paymentAccounts: List<PaymentAccount> = emptyList(),
    val password: String? = null
)
