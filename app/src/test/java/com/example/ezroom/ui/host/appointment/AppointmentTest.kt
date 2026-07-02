package com.example.ezroom.ui.host.appointment

import com.example.ezroom.domain.model.Appointment
import com.example.ezroom.domain.model.AppointmentStatus
import org.junit.Assert.assertEquals
import org.junit.Test

class AppointmentTest {

    @Test
    fun `appointment data class stores correct values`() {
        val appointment = Appointment(
            id = "1",
            roomId = "R1",
            roomName = "Room A",
            renterName = "John Doe",
            renterPhone = "0123456789",
            hostName = "Host A",
            date = "20/05/2024",
            time = "14:00",
            note = "Notes",
            status = AppointmentStatus.PENDING
        )

        assertEquals("1", appointment.id)
        assertEquals("Room A", appointment.roomName)
        assertEquals("John Doe", appointment.renterName)
        assertEquals("20/05/2024", appointment.date)
        assertEquals("14:00", appointment.time)
        assertEquals("Notes", appointment.note)
        assertEquals(AppointmentStatus.PENDING, appointment.status)
    }

    @Test
    fun `filtering appointments by status works correctly`() {
        val appointments = listOf(
            createMockAppointment("1", AppointmentStatus.PENDING),
            createMockAppointment("2", AppointmentStatus.APPROVED),
            createMockAppointment("3", AppointmentStatus.PENDING)
        )

        val pending = appointments.filter { it.status == AppointmentStatus.PENDING }
        val approved = appointments.filter { it.status == AppointmentStatus.APPROVED }
        val canceled = appointments.filter { it.status == AppointmentStatus.CANCELED }

        assertEquals(2, pending.size)
        assertEquals(1, approved.size)
        assertEquals(0, canceled.size)
    }

    private fun createMockAppointment(id: String, status: AppointmentStatus): Appointment {
        return Appointment(
            id = id,
            roomId = "R$id",
            roomName = "Room $id",
            renterName = "User $id",
            renterPhone = "000000000$id",
            hostName = "Host $id",
            date = "Date $id",
            time = "Time $id",
            note = "Note $id",
            status = status
        )
    }
}
