package com.example.ezroom.data.model

data class Invoice(
    val id: String,
    val roomId: String,
    val roomName: String,
    val period: String,
    val roomPrice: Long,
    val oldElectricity: Int,
    val newElectricity: Int,
    val oldWater: Int,
    val newWater: Int,
    val otherCosts: Long,
    val status: InvoiceStatus,
    val dateCreated: String,
    val paymentMethod: String? = null
)
