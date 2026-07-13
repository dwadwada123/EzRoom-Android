package com.example.ezroom.domain.usecase

import com.example.ezroom.core.Try
import com.example.ezroom.domain.model.*
import com.example.ezroom.domain.repository.RoomRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GetDiscoveryItemsUseCaseTest {

    private val mockRepository = object : RoomRepository {
        override fun getRooms(): Flow<List<Room>> = flowOf(
            listOf(
                createRoom("r1", "p1", RoomStatus.ACTIVE),
                createRoom("r2", "p1", RoomStatus.RENTED),
                createRoom("r3", "p1", RoomStatus.REMOVED), // Should be filtered out
                createRoom("r4", null, RoomStatus.ACTIVE)
            )
        )

        override fun getProperties(): Flow<List<Property>> = flowOf(
            listOf(
                createProperty("p1", PropertyType.COMPLEX),
                createProperty("p2", PropertyType.SINGLE)
            )
        )

        override suspend fun togglePropertyVisibility(propertyId: String) {}
        override suspend fun deleteProperty(propertyId: String) {}
        override suspend fun toggleRoomVisibility(roomId: String) {}
        override suspend fun deleteRoom(roomId: String) {}
        override suspend fun saveProperty(property: Property) {}
        override suspend fun getPropertyById(propertyId: String): Property? = null
        override suspend fun submitAppeal(roomId: String, appealText: String, images: List<String>) {}
    }

    private val useCase = GetDiscoveryItemsUseCase(mockRepository)

    @Test
    fun `usecase groups rooms by property and filters removed rooms correctly`() = runBlocking {
        val resultTry = useCase().first()
        assertTrue(resultTry is Try.Success)
        
        val result = (resultTry as Try.Success).value

        // p1 (Complex) should have r1 and r2. r3 (Removed) must be excluded.
        
        assertEquals(2, result.size)
        
        val p1Item = result.find { it.property.id == "p1" }
        assertEquals(2, p1Item?.rooms?.size) 
        assertTrue(p1Item?.rooms?.none { it.status == RoomStatus.REMOVED } == true)
        
        val p2Item = result.find { it.property.id == "p2" }
        assertEquals(0, p2Item?.rooms?.size)
    }

    private fun createRoom(id: String, propertyId: String?, status: RoomStatus): Room {
        return Room(
            id = id,
            propertyId = propertyId,
            title = "Room $id",
            price = 1000,
            priceFormatted = "1000",
            address = "",
            detailedAddress = "",
            description = "",
            structure = RoomStructure.SINGLE,
            floorArea = 20.0,
            images = emptyList(),
            amenities = emptyList(),
            status = status,
            latitude = 0.0,
            longitude = 0.0
        )
    }

    private fun createProperty(id: String, type: PropertyType): Property {
        return Property(
            id = id,
            name = "Property $id",
            type = type,
            address = "",
            detailedAddress = "",
            description = "",
            latitude = 0.0,
            longitude = 0.0
        )
    }
}
