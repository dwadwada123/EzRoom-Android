package com.example.ezroom.data.model

data class NotificationItem(
    val id: String,
    val title: String,
    val content: String,
    val time: String,
    val isRead: Boolean,
    val type: String
)

data class HostStats(
    val totalRooms: Int,
    val occupiedRooms: Int,
    val pendingAppointments: Int,
    val unpaidInvoices: Int,
    val monthlyRevenue: Long
)

data class FilterParams(
    val minPrice: Double = 0.0,
    val maxPrice: Double = 50.0,
    val structure: RoomStructure? = null,
    val amenities: List<String> = emptyList()
)
