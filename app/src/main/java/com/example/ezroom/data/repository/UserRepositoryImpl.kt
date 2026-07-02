package com.example.ezroom.data.repository

import com.example.ezroom.data.model.MockData
import com.example.ezroom.domain.model.User
import com.example.ezroom.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class UserRepositoryImpl : UserRepository {
    private val _user = MutableStateFlow<User?>(MockData.currentUser)
    
    override fun getCurrentUser(): Flow<User?> = _user.asStateFlow()

    override suspend fun updateProfile(name: String, phone: String) {
        _user.update { it?.copy(name = name, phone = phone) }
        MockData.currentUser = _user.value!!
    }

    override suspend fun verifyEkyc(idCardNumber: String, frontImage: String, backImage: String) {
        // Mocking EKYC process
        _user.update { it?.copy(isEkycVerified = true) }
        MockData.currentUser = _user.value!!
    }
}
