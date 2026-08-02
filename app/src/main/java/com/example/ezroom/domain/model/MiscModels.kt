package com.example.ezroom.domain.model

data class NotificationItem(
    val id: String,
    val title: String,
    val content: String,
    val time: String,
    val isRead: Boolean,
    val type: String,
    val targetId: String? = null
)

data class HostStats(
    val totalRooms: Int,
    val vacantRooms: Int,
    val rentedRooms: Int,
    val expectedRevenue: String,
    val totalAppointments: Int,
    val occupancyRate: Float,
    val totalContracts: Int = 0
)

data class FilterParams(
    val selectedDistrict: String = "",
    val selectedWard: String = "",
    val priceMin: Float = 0f,
    val priceMax: Float = 30f,
    val selectedAreaRange: String = "",
    val selectedRoomType: String = "",
    val selectedAmenities: List<String> = emptyList()
) : java.io.Serializable {
    val priceRange: ClosedFloatingPointRange<Float>
        get() = priceMin..priceMax
}
