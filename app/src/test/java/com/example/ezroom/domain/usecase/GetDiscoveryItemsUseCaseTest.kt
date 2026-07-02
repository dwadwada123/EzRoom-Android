package com.example.ezroom.domain.usecase

import com.example.ezroom.domain.model.*
import com.example.ezroom.domain.repository.RoomRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class GetDiscoveryItemsUseCaseTest {

    private val mockRepository = object : RoomRepository {
        override fun getRooms(): Flow<List<Room>> = flowOf(
            listOf(
                createRoom("r1", "p1", RoomStatus.ACTIVE),
                createRoom("r2", "p1", RoomStatus.RENTED),
                createRoom("r3", null, RoomStatus.ACTIVE) // Standalone
            )
        )

        override fun getProperties(): Flow<List<Property>> = flowOf(
            listOf(
                createProperty("p1", PropertyType.COMPLEX),
                createProperty("p2", PropertyType.SINGLE) // Standalone property
            )
        )
    }

    private val useCase = GetDiscoveryItemsUseCase(mockRepository)

    @Test
    fun `usecase groups rooms by property correctly`() = runBlocking {
        val result = useCase().first()

        // p1 has 1 active room (r1). r2 is rented so ignored in grouping usually or depends on logic.
        // In my UseCase: rooms = rooms.filter { it.propertyId == property.id && it.status == RoomStatus.ACTIVE }
        // So p1 should have [r1].
        // p2 is SINGLE type, it should be included even if rooms are empty in the filter (if we follow the logic)
        
        assertEquals(2, result.size)
        
        val p1Item = result.find { it.property.id == "p1" }
        assertEquals(1, p1Item?.rooms?.size)
        assertEquals("r1", p1Item?.rooms?.first()?.id)
        
        val p2Item = result.find { it.property.id == "p2" }
        assertEquals(0, p2Item?.rooms?.size) // Rooms for p2 were not in the mock list
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
