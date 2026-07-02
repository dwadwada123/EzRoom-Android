package com.example.ezroom.domain.repository

import com.example.ezroom.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getCurrentUser(): Flow<User?>
    suspend fun updateProfile(name: String, phone: String)
    suspend fun verifyEkyc(idCardNumber: String, frontImage: String, backImage: String)
}
