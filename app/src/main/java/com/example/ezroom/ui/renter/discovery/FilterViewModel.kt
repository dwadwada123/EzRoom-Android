package com.example.ezroom.ui.renter.discovery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ezroom.domain.model.FilterParams
import com.example.ezroom.domain.usecase.GetSearchMetadataUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class FilterUiState(
    val roomTypes: List<String> = emptyList(),
    val amenities: List<String> = emptyList(),
    val currentParams: FilterParams = FilterParams(),
    val isLoading: Boolean = false
)

class FilterViewModel(
    private val getSearchMetadata: GetSearchMetadataUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(FilterUiState())
    val uiState: StateFlow<FilterUiState> = _uiState.asStateFlow()

    init {
        loadMetadata()
    }

    private fun loadMetadata() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            combine(
                getSearchMetadata.getRoomTypes(),
                getSearchMetadata.getAmenities()
            ) { types, amenities ->
                _uiState.update { it.copy(roomTypes = types, amenities = amenities, isLoading = false) }
            }.collect()
        }
    }

    fun updateParams(params: FilterParams) {
        _uiState.update { it.copy(currentParams = params) }
    }

    fun resetFilters() {
        _uiState.update { it.copy(currentParams = FilterParams()) }
    }
}
