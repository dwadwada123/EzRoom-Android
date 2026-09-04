package com.example.ezroom.domain.repository

import com.example.ezroom.domain.model.Property
import com.example.ezroom.domain.model.Room
import kotlinx.coroutines.flow.Flow

// Domain Repository: Room & Property Management
interface RoomRepository {
    fun getRooms(): Flow<List<Room>>
    fun getProperties(): Flow<List<Property>>
    fun getHostRooms(): Flow<List<Room>>
    fun getHostProperties(): Flow<List<Property>>
    suspend fun getRoomsByPropertyId(propertyId: String): List<Room>
    
    suspend fun togglePropertyVisibility(propertyId: String)
    suspend fun deleteProperty(propertyId: String)
    suspend fun toggleRoomVisibility(roomId: String)
    suspend fun deleteRoom(roomId: String)
    suspend fun saveProperty(property: Property): Property
    suspend fun getPropertyById(propertyId: String): Property?
    suspend fun submitAppeal(roomId: String, appealText: String, images: List<String>)
    suspend fun getRoomById(roomId: String): Room?
    suspend fun saveRoom(room: Room): Room
    suspend fun uploadRoomImage(fileBytes: ByteArray, fileName: String, mimeType: String): String?
    suspend fun getRoomReviews(roomId: String): List<com.example.ezroom.domain.model.RoomReview>
    suspend fun submitRoomReview(roomId: String, rating: Int, comment: String): Boolean
}
