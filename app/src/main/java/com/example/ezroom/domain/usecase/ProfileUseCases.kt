package com.example.ezroom.domain.usecase

import com.example.ezroom.domain.model.User
import com.example.ezroom.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import android.content.Context
import android.net.Uri

class GetCurrentUserUseCase(private val repository: UserRepository) {
    operator fun invoke(): Flow<User?> = repository.getCurrentUser()
}

class UpdateProfileUseCase(private val repository: UserRepository) {
    suspend operator fun invoke(name: String, phone: String) = repository.updateProfile(name, phone)
}

class VerifyEkycUseCase(private val repository: UserRepository) {
    suspend operator fun invoke(idCardNumber: String, frontUri: Uri, backUri: Uri, selfieUri: Uri, context: Context) = 
        repository.verifyEkyc(idCardNumber, frontUri, backUri, selfieUri, context)
}

class ChangePasswordUseCase(private val repository: UserRepository) {
    suspend operator fun invoke(current: String, new: String): Boolean =
        repository.changePassword(current, new)
}
