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
            properties
                .filter { !it.isHidden } // Hide properties if host hid them
                .map { property ->
                    DiscoveryItem(
                        property = property,
                        rooms = rooms.filter { 
                            it.propertyId == property.id && 
                            !it.isUserHidden && // New flag
                            it.status != RoomStatus.PENDING
                        }
                    )
                }.filter { it.rooms.isNotEmpty() || it.property.type == PropertyType.SINGLE }
            .sortedByDescending { item -> 
                item.rooms.any { it.status == RoomStatus.ACTIVE } 
            }
        }.map<List<DiscoveryItem>, Try<List<DiscoveryItem>>> { 
            Try.Success(it) 
        }.catch { 
            emit(Try.Failure(AppError.Unknown(it)))
        }
    }
}
