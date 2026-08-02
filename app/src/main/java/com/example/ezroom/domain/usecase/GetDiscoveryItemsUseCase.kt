package com.example.ezroom.domain.usecase

import com.example.ezroom.core.AppError
import com.example.ezroom.core.Try
import com.example.ezroom.domain.model.DiscoveryItem
import com.example.ezroom.domain.model.PropertyType
import com.example.ezroom.domain.model.RoomStatus
import com.example.ezroom.domain.repository.RoomRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class GetDiscoveryItemsUseCase(
    private val repository: RoomRepository
) {
    operator fun invoke(): Flow<Try<List<DiscoveryItem>>> {
        return combine(
            repository.getProperties(),
            repository.getRooms()
        ) { properties, rooms ->
            val complexItems = properties
                .filter { !it.isHidden } // Hide properties if host hid them
                .map { property ->
                    val filteredRooms = rooms.filter { 
                        it.propertyId == property.id && 
                        !it.isUserHidden && 
                        it.status != RoomStatus.PENDING &&
                        it.status != RoomStatus.REMOVED
                    }
                    DiscoveryItem(
                        property = property.copy(rooms = filteredRooms),
                        rooms = filteredRooms
                    )
                }.filter { it.rooms.isNotEmpty() || it.property.type == PropertyType.SINGLE }

            val standaloneRooms = rooms.filter { room ->
                (room.propertyId == null || room.propertyId == "standalone") &&
                !room.isUserHidden &&
                room.status != RoomStatus.PENDING &&
                room.status != RoomStatus.REMOVED
            }

            val standaloneItems = standaloneRooms.map { room ->
                DiscoveryItem(
                    property = com.example.ezroom.domain.model.Property(
                        id = room.id,
                        name = room.title,
                        type = PropertyType.SINGLE,
                        address = room.address,
                        detailedAddress = room.detailedAddress,
                        description = room.description,
                        latitude = room.latitude,
                        longitude = room.longitude,
                        hostId = room.hostId ?: "",
                        rating = room.rating,
                        reviewCount = room.reviewCount,
                        rooms = listOf(room)
                    ),
                    rooms = listOf(room)
                )
            }

            (complexItems + standaloneItems).sortedByDescending { item -> 
                item.rooms.any { it.status == RoomStatus.ACTIVE } 
            }
        }.map<List<DiscoveryItem>, Try<List<DiscoveryItem>>> { 
            Try.Success(it) 
        }.catch { 
            emit(Try.Failure(AppError.Unknown(it)))
        }
    }
}
