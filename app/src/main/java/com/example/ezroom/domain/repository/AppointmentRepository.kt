package com.example.ezroom.domain.repository

import com.example.ezroom.domain.model.Appointment
import com.example.ezroom.domain.model.AppointmentStatus
import kotlinx.coroutines.flow.Flow

interface AppointmentRepository {
    fun getAppointments(): Flow<List<Appointment>>
    suspend fun updateAppointmentStatus(appointmentId: String, status: AppointmentStatus, date: String? = null, time: String? = null)
    suspend fun createAppointment(appointment: Appointment)
}
