package com.example.ezroom.data.repository

import com.example.ezroom.data.model.MockData
import com.example.ezroom.domain.model.Property
import com.example.ezroom.domain.model.Room
import com.example.ezroom.domain.repository.RoomRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

// Data Layer: Room Repository Implementation
class RoomRepositoryImpl : RoomRepository {
    
    override fun getRooms(): Flow<List<Room>> = flow {
        // Business Logic: Emit mock rooms
        emit(MockData.rooms)
    }

    override fun getProperties(): Flow<List<Property>> = flow {
        // Business Logic: Emit mock properties
        emit(MockData.properties)
    }

    override suspend fun togglePropertyVisibility(propertyId: String) {
        val index = MockData.properties.indexOfFirst { it.id == propertyId }
        if (index != -1) {
            val current = MockData.properties[index]
            MockData.properties[index] = current.copy(isHidden = !current.isHidden)
        }
    }

    override suspend fun deleteProperty(propertyId: String) {
        val index = MockData.properties.indexOfFirst { it.id == propertyId }
        if (index != -1) {
            MockData.properties.removeAt(index)
            // Cascade delete rooms
            MockData.rooms.removeAll { it.propertyId == propertyId }
        }
    }

    override suspend fun toggleRoomVisibility(roomId: String) {
        val index = MockData.rooms.indexOfFirst { it.id == roomId }
        if (index != -1) {
            val current = MockData.rooms[index]
            MockData.rooms[index] = current.copy(isUserHidden = !current.isUserHidden)
        }
    }

    override suspend fun deleteRoom(roomId: String) {
        MockData.rooms.removeAll { it.id == roomId }
    }

    override suspend fun saveProperty(property: Property) {
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
        val index = MockData.rooms.indexOfFirst { it.id == roomId }
        if (index != -1) {
            val current = MockData.rooms[index]
            val updatedInfo = current.removalInfo?.copy(
                appealText = appealText,
                appealImages = images
            )
            MockData.rooms[index] = current.copy(removalInfo = updatedInfo)
        }
    }
}
