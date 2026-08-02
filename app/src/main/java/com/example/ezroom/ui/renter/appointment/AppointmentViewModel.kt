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

    init {
        loadAppointments()
    }

    fun loadAppointments() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            delay(400)
            
            val user = com.example.ezroom.util.TokenManager.getUser()
            val name = user?.name ?: "Nguyễn Văn A"
            getAppointments(forRenter = true, userName = name)
                .onEach { result ->
                    when (result) {
                        is Try.Success -> {
                            _uiState.update { state ->
                                val filtered = when (state.selectedTabIndex) {
                                    0 -> result.value.filter { it.status == AppointmentStatus.PENDING || it.status == AppointmentStatus.RESCHEDULED }
                                    1 -> result.value.filter { it.status == AppointmentStatus.APPROVED }
                                    else -> result.value.filter { it.status == AppointmentStatus.CANCELED }
                                }
                                state.copy(
                                    appointments = filtered,
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
        loadAppointments()
    }

    fun approveAppointment(appointmentId: String) {
        viewModelScope.launch {
            updateStatus(appointmentId, AppointmentStatus.APPROVED)
            loadAppointments()
        }
    }

    fun cancelAppointment(appointmentId: String) {
        viewModelScope.launch {
            updateStatus(appointmentId, AppointmentStatus.CANCELED)
            loadAppointments()
        }
    }

    fun rescheduleAppointment(appointmentId: String, newDate: String, newTime: String) {
        viewModelScope.launch {
            updateStatus(appointmentId, AppointmentStatus.PENDING, newDate, newTime)
            loadAppointments()
        }
    }
}
