package com.example.ezroom.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ezroom.domain.model.User
import com.example.ezroom.domain.usecase.GetCurrentUserUseCase
import com.example.ezroom.domain.usecase.UpdateProfileUseCase
import com.example.ezroom.domain.usecase.VerifyEkycUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

import android.content.Context
import android.net.Uri

data class ProfileUiState(
    val user: User? = null,
    val isLoading: Boolean = false,
    val isEkycSuccess: Boolean = false,
    val errorMessage: String? = null
)

class ProfileViewModel(
    private val getCurrentUser: GetCurrentUserUseCase,
    private val updateProfile: UpdateProfileUseCase,
    private val verifyEkyc: VerifyEkycUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            getCurrentUser()
                .onEach { user ->
                    _uiState.update { it.copy(user = user, isLoading = false) }
                }
                .collect()
        }
    }

    fun onUpdateProfile(name: String, phone: String) {
        viewModelScope.launch {
            updateProfile(name, phone)
        }
    }

    fun onVerifyEkyc(idCardNumber: String, frontUri: Uri, backUri: Uri, selfieUri: Uri, context: Context) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = verifyEkyc(idCardNumber, frontUri, backUri, selfieUri, context)
            if (result.isSuccess) {
                _uiState.update { it.copy(isLoading = false, isEkycSuccess = true) }
            } else {
                _uiState.update { it.copy(isLoading = false, errorMessage = result.exceptionOrNull()?.message ?: "Có lỗi xảy ra") }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
