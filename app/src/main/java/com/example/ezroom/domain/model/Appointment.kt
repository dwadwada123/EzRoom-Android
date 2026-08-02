package com.example.ezroom.domain.model

data class Appointment(
    @com.google.gson.annotations.SerializedName("_id") val id: String,
    val roomId: String,
    val roomName: String,
    val renterId: String? = null,
    val renterName: String,
    val renterPhone: String,
    val hostName: String,
    val date: String,
    val time: String,
    val note: String,
    val status: AppointmentStatus
)
