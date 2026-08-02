package com.example.ezroom.domain.model

data class DetailedArea(
    val id: String,
    val roomName: String,
    val areaValue: Double,
)

// Property model
data class Property(
    val id: String,
    val name: String,
    val type: PropertyType,
    val address: String,
    val detailedAddress: String,
    val description: String,
    val commonAmenities: List<Amenity> = emptyList(),
    val images: List<RoomImage> = emptyList(),
    val latitude: Double,
    val longitude: Double,
    val rooms: List<Room> = emptyList(),
    val isHidden: Boolean = false,
    val hostId: String = "",
    val rating: Float = 0f,
    val reviewCount: Int = 0
) {

    // UI Helpers
    val vacantRoomCount: Int get() = rooms.count { it.status == RoomStatus.ACTIVE }
    
    val priceRange: String get() {
        if (rooms.isEmpty()) return "Liên hệ"
        val minPrice = rooms.minOf { it.price }
        val maxPrice = rooms.maxOf { it.price }
        val formatter = java.text.DecimalFormat("#,###")
        return if (minPrice == maxPrice) {
            "${formatter.format(minPrice)} đ"
        } else {
            "${formatter.format(minPrice)} - ${formatter.format(maxPrice)} đ"
        }
    }
}

data class Room(
    val id: String,
    val propertyId: String? = null,
    val title: String,
    val price: Long,
    val priceFormatted: String,
    val electricityPrice: Long = 3500L,
    val waterPrice: Long = 15000L,
    val address: String,
    val detailedAddress: String,
    val description: String,
    val structure: RoomStructure = RoomStructure.SINGLE,
    val floorArea: Double,
    val mezzanineArea: Double = 0.0,
    val capacity: Int = 0,
    val detailedAreas: List<DetailedArea> = emptyList(),
    val rating: Float = 0f,
    val reviewCount: Int = 0,
    val reviews: List<RoomReview> = emptyList(),
    val images: List<RoomImage>,
    val amenities: List<Amenity>,
    val status: RoomStatus = RoomStatus.ACTIVE,
    val latitude: Double,
    val longitude: Double,
    val currentRenter: RenterInfo? = null,
    val pastRenters: List<RenterInfo> = emptyList(),
    val isUserHidden: Boolean = false,
    val removalInfo: RoomRemovalInfo? = null,
    val hostId: String? = null,
    val hostName: String? = null,
    val hostPhone: String? = null,
    val hostAvatarUrl: String? = null,
)

data class RenterInfo(
    val id: String,
    val name: String,
    val phone: String,
    val avatarUrl: String? = null,
    val stayPeriod: String, // e.g., "01/2024 - Hiện tại" or "01/2023 - 12/2023"
    val isCurrentlyStaying: Boolean
)

data class RoomReview(
    @com.google.gson.annotations.SerializedName("_id", alternate = ["id"])
    val id: String,
    val userName: String = "Người thuê",
    val userAvatar: Int? = null,
    val rating: Int,
    val comment: String,
    val date: String = "",
    val hostReply: String? = null, // Host's response to renter's review
    val isReported: Boolean = false // If the host thinks this review is unfair
)

data class RenterReview(
    @com.google.gson.annotations.SerializedName("_id", alternate = ["id"])
    val id: String,
    val renterId: String = "",
    val hostName: String,
    val hostAvatar: Int? = null,
    val rating: Int,
    val tags: List<String> = emptyList(),
    val comment: String,
    val date: String,
    val renterReply: String? = null, // Renter's response to host's review
    val isReported: Boolean = false // If the renter thinks this review is unfair
)
