package com.example.ezroom.data.model

data class Contract(
    val id: String,
    val roomId: String,
    val roomName: String,
    val renterName: String,
    val renterPhone: String,
    val startDate: String,
    val endDate: String,
    val depositAmount: Long,
    val depositStatus: DepositStatus,
    val dateSigned: String? = null
)
