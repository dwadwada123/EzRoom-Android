package com.example.ezroom.data.repository

import com.example.ezroom.domain.model.Appointment
import com.example.ezroom.domain.model.AppointmentStatus
import com.example.ezroom.domain.repository.AppointmentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AppointmentRepositoryImpl(
    private val api: com.example.ezroom.data.remote.AppointmentApi = com.example.ezroom.data.remote.AppointmentApi.create()
) : AppointmentRepository {
    override fun getAppointments(): Flow<List<Appointment>> = flow {
        try {
            val user = com.example.ezroom.util.TokenManager.getUser()
            val renterName = if (user?.role == "RENTER") user.name else null
            val hostName = if (user?.role == "HOST") user.name else null
            val list = api.getAppointments(renterName, hostName)
            emit(list)
        } catch (e: java.lang.Exception) {
            emit(emptyList())
        }
    }

    override suspend fun updateAppointmentStatus(appointmentId: String, status: AppointmentStatus, date: String?, time: String?) {
        try {
            api.updateAppointmentStatus(
                appointmentId,
                com.example.ezroom.data.remote.UpdateAppointmentStatusRequest(status.name, date, time)
            )
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun createAppointment(appointment: Appointment) {
        try {
            api.createAppointment(appointment)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }
}
