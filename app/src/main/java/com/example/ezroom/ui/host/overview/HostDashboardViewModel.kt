package com.example.ezroom.ui.host.overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ezroom.domain.model.HostStats
import com.example.ezroom.domain.usecase.GetHostStatsUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class HostDashboardUiState(
    val stats: HostStats? = null,
    val isLoading: Boolean = false,
    val selectedTimeRange: String = "Tháng này"
)

class HostDashboardViewModel(
    private val getHostStats: GetHostStatsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HostDashboardUiState())
    val uiState: StateFlow<HostDashboardUiState> = _uiState.asStateFlow()

    init {
        loadStats()
    }

    private fun loadStats() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            delay(800)
            
            getHostStats(_uiState.value.selectedTimeRange)
                .onEach { stats ->
                    _uiState.update { it.copy(stats = stats, isLoading = false) }
                }
                .collect()
        }
    }

    fun onTimeRangeSelected(range: String) {
        _uiState.update { it.copy(selectedTimeRange = range) }
        loadStats()
    }
}
