package com.example.ezroom.domain.usecase

import com.example.ezroom.domain.model.AppointmentStatus
import com.example.ezroom.domain.repository.AppointmentRepository

class UpdateAppointmentStatusUseCase(
    private val repository: AppointmentRepository
) {
    suspend operator fun invoke(appointmentId: String, status: AppointmentStatus, date: String? = null, time: String? = null) {
        repository.updateAppointmentStatus(appointmentId, status, date, time)
    }
}
