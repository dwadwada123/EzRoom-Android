package com.example.ezroom.ui.host.appointment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ezroom.domain.model.Appointment
import com.example.ezroom.domain.model.AppointmentStatus
import com.example.ezroom.domain.usecase.GetAppointmentsUseCase
import com.example.ezroom.domain.usecase.UpdateAppointmentStatusUseCase
import com.example.ezroom.domain.repository.AppointmentRepository
import com.example.ezroom.core.Try
import com.example.ezroom.data.model.MockData
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class HostAppointmentUiState(
    val appointments: List<Appointment> = emptyList(),
    val isLoading: Boolean = false,
    val selectedTabIndex: Int = 0
)

class HostAppointmentViewModel(
    private val getAppointments: GetAppointmentsUseCase,
    private val updateStatus: UpdateAppointmentStatusUseCase,
    private val repository: AppointmentRepository // Needed for direct MockData updates if not using a better flow
) : ViewModel() {

    private val _uiState = MutableStateFlow(HostAppointmentUiState())
    val uiState: StateFlow<HostAppointmentUiState> = _uiState.asStateFlow()

    init {
        loadAppointments()
    }

    private fun loadAppointments() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            delay(800)
            
            getAppointments(forRenter = false, userName = "Lê Văn Chủ")
                .onEach { result ->
                    _uiState.update { state ->
                        val appointments = if (result is Try.Success) {
                            val list = result.value
                            when (state.selectedTabIndex) {
                                0 -> list.filter { it.status == AppointmentStatus.PENDING || it.status == AppointmentStatus.RESCHEDULED }
                                1 -> list.filter { it.status == AppointmentStatus.APPROVED }
                                else -> list.filter { it.status == AppointmentStatus.CANCELED }
                            }
                        } else {
                            emptyList()
                        }
                        state.copy(appointments = appointments, isLoading = false)
                    }
                }
                .collect()
        }
    }

    fun onTabSelected(index: Int) {
        _uiState.update { it.copy(selectedTabIndex = index) }
        loadAppointments()
    }

    fun updateAppointmentStatus(appointmentId: String, status: AppointmentStatus) {
        viewModelScope.launch {
            updateStatus(appointmentId, status)
            loadAppointments()
        }
    }

    fun rescheduleAppointment(appointmentId: String, newDate: String, newTime: String) {
        viewModelScope.launch {
            val index = MockData.appointments.indexOfFirst { it.id == appointmentId }
            if (index != -1) {
                val updated = MockData.appointments[index].copy(
                    date = newDate,
                    time = newTime,
                    status = AppointmentStatus.RESCHEDULED
                )
                MockData.appointments[index] = updated
            }
            loadAppointments()
        }
    }
}
