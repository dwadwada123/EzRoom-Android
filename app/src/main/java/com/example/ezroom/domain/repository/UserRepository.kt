package com.example.ezroom.domain.repository

import com.example.ezroom.domain.model.User
import kotlinx.coroutines.flow.Flow

import android.content.Context
import android.net.Uri

interface UserRepository {
    fun getCurrentUser(): Flow<User?>
    suspend fun updateProfile(name: String, phone: String)
    suspend fun verifyEkyc(idCardNumber: String, frontUri: Uri, backUri: Uri, selfieUri: Uri, context: Context): Result<Unit>
    suspend fun changePassword(current: String, new: String): Boolean
    suspend fun login(email: String, password: String): Boolean
    suspend fun register(name: String, email: String, phone: String, password: String, role: String): Boolean
    suspend fun requestForgotPassword(email: String): Result<String>
    suspend fun resetPassword(email: String, otp: String, newPass: String): Result<String>
}

