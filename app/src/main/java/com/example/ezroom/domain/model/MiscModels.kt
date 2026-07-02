package com.example.ezroom.domain.model

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
    val vacantRooms: Int,
    val rentedRooms: Int,
    val expectedRevenue: String,
    val totalAppointments: Int,
    val occupancyRate: Float
)

data class FilterParams(
    val selectedDistrict: String = "",
    val selectedWard: String = "",
    val priceRange: ClosedFloatingPointRange<Float> = 1f..10f,
    val selectedAreaRange: String = "",
    val selectedAmenities: List<String> = emptyList()
)
