package com.example.ezroom.data.model

data class Appointment(
    val id: String,
    val roomId: String,
    val roomName: String,
    val renterName: String,
    val renterPhone: String,
    val hostName: String,
    val date: String,
    val time: String,
    val note: String,
    val status: AppointmentStatus
)
