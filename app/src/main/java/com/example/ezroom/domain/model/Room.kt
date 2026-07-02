package com.example.ezroom.domain.model

data class DetailedArea(
    val id: String,
    val roomName: String,
    val areaValue: Double
)

/**
 * Property represents a Building or Standalone House.
 * High-level grouping for Host Management and Renter Discovery.
 */
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
    val isHidden: Boolean = false // New: Property-level visibility
) {
    // UI Helpers
    val vacantRoomCount: Int get() = rooms.count { it.status == RoomStatus.ACTIVE }
    val totalRoomCount: Int get() = rooms.size
    val priceRange: String get() {
        if (rooms.isEmpty()) return "Liên hệ"
        val minPrice = rooms.minOf { it.price }
        val maxPrice = rooms.maxOf { it.price }
        return if (minPrice == maxPrice) {
            "${minPrice / 1_000_000.0}tr"
        } else {
            "${minPrice / 1_000_000.0}tr - ${maxPrice / 1_000_000.0}tr"
        }
    }
}

data class Room(
    val id: String,
    val propertyId: String? = null, // Links to a Property Building
    val title: String, // E.g., "Phòng 101" or "Nhà nguyên căn Thủ Đức"
    val price: Long,
    val priceFormatted: String,
    val electricityPrice: Long = 3500L,
    val waterPrice: Long = 15000L,
    val address: String,
    val detailedAddress: String,
    val description: String,
    val structure: RoomStructure,
    val floorArea: Double,
    val mezzanineArea: Double = 0.0,
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
    val isUserHidden: Boolean = false // New: Manual hide by host
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
    val id: String,
    val userName: String,
    val userAvatar: Int? = null,
    val rating: Int,
    val comment: String,
    val date: String,
    val hostReply: String? = null, // Host's response to renter's review
    val isReported: Boolean = false // If the host thinks this review is unfair
)

data class RenterReview(
    val id: String,
    val hostName: String,
    val hostAvatar: Int? = null,
    val rating: Int,
    val tags: List<String> = emptyList(),
    val comment: String,
    val date: String,
    val renterReply: String? = null, // Renter's response to host's review
    val isReported: Boolean = false // If the renter thinks this review is unfair
)
