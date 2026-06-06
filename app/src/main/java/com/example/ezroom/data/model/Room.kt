package com.example.ezroom.data.model

data class DetailedArea(
    val id: String,
    val roomName: String,
    val areaValue: Double
)

data class Room(
    val id: String,
    val title: String,
    val price: Long,
    val priceFormatted: String,
    val address: String,
    val detailedAddress: String,
    val description: String,
    val structure: RoomStructure,
    val floorArea: Double,
    val mezzanineArea: Double,
    val detailedAreas: List<DetailedArea> = emptyList(),
    val rating: Float,
    val images: List<RoomImage>,
    val amenities: List<Amenity>,
    val status: RoomStatus = RoomStatus.ACTIVE,
    val latitude: Double,
    val longitude: Double
)
