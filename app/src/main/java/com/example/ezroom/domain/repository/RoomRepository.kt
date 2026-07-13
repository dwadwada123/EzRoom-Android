package com.example.ezroom.domain.repository

import com.example.ezroom.domain.model.Property
import com.example.ezroom.domain.model.Room
import kotlinx.coroutines.flow.Flow

// Domain Repository: Room & Property Management
interface RoomRepository {
    fun getRooms(): Flow<List<Room>>
    fun getProperties(): Flow<List<Property>>
    
    suspend fun togglePropertyVisibility(propertyId: String)
    suspend fun deleteProperty(propertyId: String)
    suspend fun toggleRoomVisibility(roomId: String)
    suspend fun deleteRoom(roomId: String)
    suspend fun saveProperty(property: Property)
    suspend fun getPropertyById(propertyId: String): Property?
    suspend fun submitAppeal(roomId: String, appealText: String, images: List<String>)
}
