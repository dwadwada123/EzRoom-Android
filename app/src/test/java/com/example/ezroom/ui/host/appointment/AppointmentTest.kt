package com.example.ezroom.ui.host.appointment

import org.junit.Assert.assertEquals
import org.junit.Test

class AppointmentTest {

    @Test
    fun `appointment data class stores correct values`() {
        val appointment = Appointment(
            id = "1",
            roomName = "Room A",
            personName = "John Doe",
            date = "20/05/2024",
            time = "14:00",
            note = "Notes",
            status = "Pending"
        )

        assertEquals("1", appointment.id)
        assertEquals("Room A", appointment.roomName)
        assertEquals("John Doe", appointment.personName)
        assertEquals("20/05/2024", appointment.date)
        assertEquals("14:00", appointment.time)
        assertEquals("Notes", appointment.note)
        assertEquals("Pending", appointment.status)
    }

    @Test
    fun `filtering appointments by status works correctly`() {
        val appointments = listOf(
            Appointment("1", "Room A", "User A", "Date A", "Time A", "Note A", "Pending"),
            Appointment("2", "Room B", "User B", "Date B", "Time B", "Note B", "Approved"),
            Appointment("3", "Room C", "User C", "Date C", "Time C", "Note C", "Pending")
        )

        val pending = appointments.filter { it.status == "Pending" }
        val approved = appointments.filter { it.status == "Approved" }
        val canceled = appointments.filter { it.status == "Canceled" }

        assertEquals(2, pending.size)
        assertEquals(1, approved.size)
        assertEquals(0, canceled.size)
    }
}
