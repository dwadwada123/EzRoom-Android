package com.example.ezroom.data.repository

import com.example.ezroom.data.model.MockData
import com.example.ezroom.domain.model.Appointment
import com.example.ezroom.domain.model.AppointmentStatus
import com.example.ezroom.domain.repository.AppointmentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AppointmentRepositoryImpl : AppointmentRepository {
    override fun getAppointments(): Flow<List<Appointment>> = flow {
        emit(MockData.appointments)
    }

    override suspend fun updateAppointmentStatus(appointmentId: String, status: AppointmentStatus) {
        val index = MockData.appointments.indexOfFirst { it.id == appointmentId }
        if (index != -1) {
            val updated = MockData.appointments[index].copy(status = status)
            MockData.appointments[index] = updated
        }
    }
}
