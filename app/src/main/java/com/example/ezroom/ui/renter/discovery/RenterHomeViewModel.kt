package com.example.ezroom.ui.renter.discovery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ezroom.core.AppError
import com.example.ezroom.core.Try
import com.example.ezroom.domain.model.DiscoveryItem
import com.example.ezroom.domain.usecase.GetDiscoveryItemsUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class RenterHomeUiState(
    val discoveryItems: List<DiscoveryItem> = emptyList(),
    val isLoading: Boolean = false,
    val query: String = "",
    val error: String? = null
)

class RenterHomeViewModel(
    private val getDiscoveryItems: GetDiscoveryItemsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RenterHomeUiState())
    val uiState: StateFlow<RenterHomeUiState> = _uiState.asStateFlow()

    init {
        loadDiscoveryItems()
    }

    private fun loadDiscoveryItems() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            delay(800) 
            
            getDiscoveryItems()
                .onEach { result ->
                    when (result) {
                        is Try.Success -> {
                            _uiState.update { it.copy(discoveryItems = result.value, isLoading = false) }
                        }
                        is Try.Failure -> {
                            _uiState.update { it.copy(
                                error = result.error.toMessage(), 
                                isLoading = false 
                            ) }
                        }
                    }
                }
                .collect()
        }
    }

    fun onQueryChange(newQuery: String) {
        _uiState.update { it.copy(query = newQuery) }
    }

    fun refresh() {
        loadDiscoveryItems()
    }
}

// Extension to map domain errors to user-friendly messages
fun AppError.toMessage(): String = when (this) {
    is AppError.Network -> "Không có kết nối internet. Vui lòng kiểm tra lại."
    is AppError.Database -> "Lỗi truy xuất dữ liệu từ thiết bị."
    is AppError.Unauthorized -> "Phiên đăng nhập hết hạn. Vui lòng đăng nhập lại."
    is AppError.Validation -> this.message
    is AppError.NotFound -> "Không tìm thấy nội dung yêu cầu."
    is AppError.Unknown -> "Đã có lỗi xảy ra. Vui lòng thử lại sau."
}
