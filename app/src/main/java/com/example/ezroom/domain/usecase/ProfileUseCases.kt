package com.example.ezroom.domain.usecase

import com.example.ezroom.domain.model.User
import com.example.ezroom.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow

class GetCurrentUserUseCase(private val repository: UserRepository) {
    operator fun invoke(): Flow<User?> = repository.getCurrentUser()
}

class UpdateProfileUseCase(private val repository: UserRepository) {
    suspend operator fun invoke(name: String, phone: String) = repository.updateProfile(name, phone)
}

class VerifyEkycUseCase(private val repository: UserRepository) {
    suspend operator fun invoke(idCardNumber: String, frontImage: String, backImage: String) = 
        repository.verifyEkyc(idCardNumber, frontImage, backImage)
}
