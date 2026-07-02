package com.example.ezroom.ui.renter.appointment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ezroom.core.Try
import com.example.ezroom.domain.model.Appointment
import com.example.ezroom.domain.model.AppointmentStatus
import com.example.ezroom.domain.usecase.GetAppointmentsUseCase
import com.example.ezroom.domain.usecase.UpdateAppointmentStatusUseCase
import com.example.ezroom.ui.renter.discovery.toMessage
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class AppointmentUiState(
    val appointments: List<Appointment> = emptyList(),
    val isLoading: Boolean = false,
    val selectedTabIndex: Int = 0,
    val error: String? = null
)

class AppointmentViewModel(
    private val getAppointments: GetAppointmentsUseCase,
    private val updateStatus: UpdateAppointmentStatusUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AppointmentUiState())
    val uiState: StateFlow<AppointmentUiState> = _uiState.asStateFlow()

    private val statusFilters = listOf(AppointmentStatus.PENDING, AppointmentStatus.APPROVED, AppointmentStatus.CANCELED)

    init {
        loadAppointments()
    }

    fun loadAppointments() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            delay(800)
            
            getAppointments(forRenter = true, userName = "Nguyễn Văn A")
                .onEach { result ->
                    when (result) {
                        is Try.Success -> {
                            _uiState.update { state ->
                                state.copy(
                                    appointments = result.value.filter { it.status == statusFilters[state.selectedTabIndex] },
                                    isLoading = false
                                )
                            }
                        }
                        is Try.Failure -> {
                            _uiState.update { it.copy(isLoading = false, error = result.error.toMessage()) }
                        }
                    }
                }
                .collect()
        }
    }

    fun onTabSelected(index: Int) {
        _uiState.update { it.copy(selectedTabIndex = index) }
        loadAppointments() // Re-filter
    }

    fun cancelAppointment(appointmentId: String) {
        viewModelScope.launch {
            updateStatus(appointmentId, AppointmentStatus.CANCELED)
            loadAppointments()
        }
    }
}
