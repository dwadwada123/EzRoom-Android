package com.example.ezroom.data.repository

import com.example.ezroom.data.model.MockData
import com.example.ezroom.data.remote.NetworkClient
import com.example.ezroom.data.remote.PropertyApi
import com.example.ezroom.data.remote.PropertyRequest
import com.example.ezroom.data.remote.PropertyResponse
import com.example.ezroom.data.remote.RoomApi
import com.example.ezroom.data.remote.RoomRequest
import com.example.ezroom.domain.model.RoomStructure
import com.example.ezroom.domain.model.RoomStatus

import com.example.ezroom.domain.model.Property
import com.example.ezroom.domain.model.Room
import com.example.ezroom.domain.repository.RoomRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody

class RoomRepositoryImpl : RoomRepository {
    private val propertyApi = NetworkClient.createService<PropertyApi>()
    private val roomApi = NetworkClient.createService<RoomApi>()
    
    override fun getRooms(): Flow<List<Room>> = flow {
        try {
            // Public endpoint for discovery
            val responseList = roomApi.getRooms()
            val domainList = responseList.map { mapRoomResponseToDomain(it) }
            
            // Merge into MockData so AppNavigation can find the rooms by ID
            domainList.forEach { room ->
                val index = MockData.rooms.indexOfFirst { it.id == room.id }
                if (index != -1) MockData.rooms[index] = room else MockData.rooms.add(room)
            }
            
            emit(domainList)
        } catch (e: Exception) {
            android.util.Log.e("RoomRepo", "getRooms public error: ${e.message}")
            emit(emptyList())
        }
    }

    override fun getHostRooms(): Flow<List<Room>> = flow {
        try {
            // Use host endpoint to get ALL rooms for the host's properties
            val responseList = roomApi.getHostRooms()
            val domainList = responseList.map { mapRoomResponseToDomain(it) }
            MockData.rooms.clear()
            MockData.rooms.addAll(domainList)
            emit(domainList)
        } catch (e: Exception) {
            android.util.Log.e("RoomRepo", "getHostRooms error: ${e.message}")
            emit(MockData.rooms.toList())
        }
    }

    private fun mapRoomResponseToDomain(r: com.example.ezroom.data.remote.RoomResponse): Room {
        return Room(
            id = r.resolvedId,
            propertyId = r.propertyId,
            title = r.title,
            price = r.price,
            priceFormatted = "${r.price} đ",
            electricityPrice = r.electricityPrice,
            waterPrice = r.waterPrice,
            address = r.address,
            detailedAddress = r.detailedAddress,
            description = r.description,
            structure = try { RoomStructure.valueOf(r.structure) } catch (e: Exception) { RoomStructure.SINGLE },
            floorArea = r.floorArea,
            mezzanineArea = r.mezzanineArea ?: 0.0,
            capacity = r.capacity ?: 0,
            images = r.images?.map { img ->
                com.example.ezroom.domain.model.RoomImage(
                    url = img["url"] ?: "",
                    category = img["category"] ?: "Khác"
                )
            } ?: emptyList(),
            amenities = r.amenities?.map { am ->
                com.example.ezroom.domain.model.Amenity(
                    name = am["name"] as? String ?: "",
                    compensationAmount = (am["compensationAmount"] as? Number)?.toLong() ?: 0L
                )
            } ?: emptyList(),
            status = try { RoomStatus.valueOf(r.status) } catch (e: Exception) { RoomStatus.ACTIVE },
            latitude = r.latitude,
            longitude = r.longitude,
            isUserHidden = r.isUserHidden,
            hostId = r.hostId,
            hostName = r.hostName,
            hostPhone = r.hostPhone,
            removalInfo = r.removalInfo?.let {
                com.example.ezroom.domain.model.RoomRemovalInfo(
                    reason = it.reason ?: "Vi phạm chính sách nền tảng",
                    removedDate = it.dateRemoved ?: "Mới đây",
                    autoDeleteDate = "30 ngày sau",
                    appealText = it.appealText,
                    appealImages = it.appealImages ?: emptyList(),
                    appealStatus = it.appealStatus
                )
            },
            rating = r.rating,
            reviewCount = r.reviewCount
        )
    }

    override fun getProperties(): Flow<List<Property>> = flow {
        try {
            // Public endpoint
            val responseList = propertyApi.getProperties()
            val domainList = responseList.map { mapPropertyResponseToDomain(it) }
            
            // Merge into MockData so AppNavigation can find the properties by ID
            domainList.forEach { prop ->
                val index = MockData.properties.indexOfFirst { it.id == prop.id }
                if (index != -1) MockData.properties[index] = prop else MockData.properties.add(prop)
            }
            
            emit(domainList)
        } catch (e: Exception) {
            android.util.Log.e("RoomRepo", "getProperties public error: ${e.message}")
            emit(emptyList())
        }
    }

    override fun getHostProperties(): Flow<List<Property>> = flow {
        try {
            // Host endpoint
            val responseList = propertyApi.getHostProperties()
            val domainList = responseList.map { mapPropertyResponseToDomain(it) }
            MockData.properties.clear()
            MockData.properties.addAll(domainList)
            emit(domainList)
        } catch (e: Exception) {
            android.util.Log.e("RoomRepo", "getHostProperties error: ${e.message}")
            emit(MockData.properties.toList())
        }
    }
    override suspend fun getRoomsByPropertyId(propertyId: String): List<Room> {
        return try {
            val rooms = roomApi.getRooms().map { mapRoomResponseToDomain(it) }
            rooms.filter { it.propertyId == propertyId }
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun mapPropertyResponseToDomain(r: com.example.ezroom.data.remote.PropertyResponse): Property {
        return Property(
            id = r.resolvedId,
            name = r.name ?: "",
            type = com.example.ezroom.domain.model.PropertyType.valueOf(r.type ?: "COMPLEX"),
            address = r.address ?: "",
            detailedAddress = r.detailedAddress ?: "",
            description = r.description ?: "",
            commonAmenities = r.commonAmenities?.map { name ->
                com.example.ezroom.domain.model.Amenity(name)
            } ?: emptyList(),
            latitude = r.latitude ?: 0.0,
            longitude = r.longitude ?: 0.0,
            isHidden = r.isHidden ?: false,
            hostId = r.hostId ?: "",
            rating = r.rating ?: 0f,
            reviewCount = r.reviewCount ?: 0
        )
    }


    override suspend fun togglePropertyVisibility(propertyId: String) {
        try {
            propertyApi.togglePropertyVisibility(propertyId)
        } catch (e: Exception) {
            android.util.Log.e("RoomRepo", "togglePropertyVisibility API error: ${e.message}")
        }
    }

    override suspend fun deleteProperty(propertyId: String) {
        val index = MockData.properties.indexOfFirst { it.id == propertyId }
        if (index != -1) {
            MockData.properties.removeAt(index)
            MockData.rooms.removeAll { it.propertyId == propertyId }
        }
    }

    override suspend fun toggleRoomVisibility(roomId: String) {
        try {
            roomApi.toggleRoomVisibility(roomId)
        } catch (e: Exception) {
            android.util.Log.e("RoomRepo", "toggleRoomVisibility API error: ${e.message}")
        }
    }

    override suspend fun deleteRoom(roomId: String) {
        try {
            roomApi.deleteRoom(roomId)
        } catch (e: Exception) {
            android.util.Log.e("RoomRepo", "deleteRoom error: ${e.message}")
        }
        MockData.rooms.removeAll { it.id == roomId }
    }

    override suspend fun saveProperty(property: Property) {
        try {
            val request = PropertyRequest(
                id = property.id.takeIf { it.isNotBlank() },
                name = property.name,
                type = property.type.name,
                address = property.address,
                detailedAddress = property.detailedAddress,
                description = property.description,
                commonAmenities = property.commonAmenities.map { it.name },
                latitude = property.latitude,
                longitude = property.longitude,
                hostId = property.hostId
            )
            propertyApi.createProperty(request)
        } catch (e: Exception) {
            android.util.Log.e("RoomRepo", "saveProperty API error: ${e.message}")
        }
        val index = MockData.properties.indexOfFirst { it.id == property.id }
        if (index != -1) {
            MockData.properties[index] = property
        } else {
            MockData.properties.add(property)
        }
    }

    override suspend fun getPropertyById(propertyId: String): Property? {
        return MockData.properties.find { it.id == propertyId }
    }

    override suspend fun submitAppeal(roomId: String, appealText: String, images: List<String>) {
        try {
            val response = roomApi.submitRoomAppeal(roomId, com.example.ezroom.data.remote.AppealRequest(appealText = appealText, images = images))
            android.util.Log.d("RoomRepo", "submitAppeal API success: ${response.success}, message: ${response.message}")
        } catch (e: Exception) {
            android.util.Log.e("RoomRepo", "submitAppeal API error: ${e.message}", e)
        }
        val index = MockData.rooms.indexOfFirst { it.id == roomId }
        if (index != -1) {
            val current = MockData.rooms[index]
            val updatedInfo = (current.removalInfo ?: com.example.ezroom.domain.model.RoomRemovalInfo(
                reason = "Vi phạm chính sách",
                removedDate = "Mới đây",
                autoDeleteDate = "Mới đây"
            )).copy(
                appealText = appealText,
                appealImages = images,
                appealStatus = "PENDING"
            )
            MockData.rooms[index] = current.copy(removalInfo = updatedInfo)
        }
    }

    override suspend fun saveRoom(room: Room) {
        try {
            val request = RoomRequest(
                id = room.id.takeIf { it.isNotBlank() },
                propertyId = room.propertyId,
                title = room.title,
                price = room.price,
                electricityPrice = room.electricityPrice,
                waterPrice = room.waterPrice,
                address = room.address,
                detailedAddress = room.detailedAddress,
                description = room.description,
                structure = room.structure.name,
                floorArea = room.floorArea,
                mezzanineArea = room.mezzanineArea,
                capacity = room.capacity,
                detailedAreas = room.detailedAreas.map { mapOf("id" to it.id, "roomName" to it.roomName, "areaValue" to it.areaValue) },
                images = room.images
                    .filter { it.url != null }
                    .map { mapOf("url" to it.url, "category" to (it.category ?: "Khác")) },
                amenities = room.amenities.map { mapOf("name" to it.name, "compensationAmount" to it.compensationAmount) },
                latitude = room.latitude,
                longitude = room.longitude,
                status = room.status.name
            )
            roomApi.createRoom(request)
        } catch (e: Exception) {
            android.util.Log.e("RoomRepo", "saveRoom API error: ${e.message}")
        }
        val index = MockData.rooms.indexOfFirst { it.id == room.id }
        if (index != -1) {
            MockData.rooms[index] = room
        } else {
            MockData.rooms.add(room)
        }
    }

    override suspend fun getRoomById(roomId: String): Room? {
        // Fallback to mock data if API is not available
        return MockData.rooms.find { it.id == roomId }
    }

    private val reviewApi = NetworkClient.createService<com.example.ezroom.data.remote.RoomReviewApi>()

    override suspend fun uploadRoomImage(fileBytes: ByteArray, fileName: String, mimeType: String): String? {
        return try {
            val mediaApi = com.example.ezroom.data.remote.MediaApi.create()
            val mediaType = mimeType.toMediaTypeOrNull()
            val requestBody = fileBytes.toRequestBody(mediaType)
            val body = okhttp3.MultipartBody.Part.createFormData("file", fileName, requestBody)
            val response = mediaApi.uploadMedia(body)
            if (response.success) response.url else null
        } catch (e: Exception) {
            android.util.Log.e("RoomRepo", "uploadRoomImage error: ${e.message}")
            null
        }
    }

    override suspend fun getRoomReviews(roomId: String): List<com.example.ezroom.domain.model.RoomReview> {
        return try {
            val response = reviewApi.getRoomReviews(roomId)
            response.map {
                com.example.ezroom.domain.model.RoomReview(
                    id = it.id,
                    userName = it.reviewerName,
                    userAvatar = null, // Avatar from string url not supported yet by int
                    rating = it.rating,
                    comment = it.comment,
                    date = it.createdAt.take(10) // Basic format
                )
            }
        } catch (e: Exception) {
            android.util.Log.e("RoomRepo", "getRoomReviews error: ${e.message}")
            emptyList()
        }
    }

    override suspend fun submitRoomReview(roomId: String, rating: Int, comment: String): Boolean {
        return try {
            val request = com.example.ezroom.data.remote.RoomReviewRequest(roomId, rating, comment)
            val response = reviewApi.createRoomReview(request)
            response.success
        } catch (e: Exception) {
            android.util.Log.e("RoomRepo", "submitRoomReview error: ${e.message}")
            false
        }
    }
}
