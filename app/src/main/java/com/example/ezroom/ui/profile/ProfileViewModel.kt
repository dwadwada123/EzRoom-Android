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

import com.example.ezroom.util.TokenManager

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

    private val _uiState = MutableStateFlow(ProfileUiState(user = TokenManager.getUser()))
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            val cached = TokenManager.getUser()
            if (cached != null && cached.id.isNotBlank()) {
                _uiState.update { it.copy(user = cached) }
            }
            getCurrentUser()
                .onEach { user ->
                    if (user != null) {
                        _uiState.update { it.copy(user = user, isLoading = false) }
                    }
                }
                .collect()
        }
        refreshProfile()
    }

    fun refreshProfile() {
        viewModelScope.launch {
            val cached = TokenManager.getUser()
            if (cached != null && cached.id.isNotBlank()) {
                _uiState.update { it.copy(user = cached) }
            }
            try {
                val fresh = getCurrentUser.refresh()
                if (fresh != null) {
                    _uiState.update { it.copy(user = fresh) }
                }
            } catch (e: Exception) {
                // Ignore network error on silent refresh
            }
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
