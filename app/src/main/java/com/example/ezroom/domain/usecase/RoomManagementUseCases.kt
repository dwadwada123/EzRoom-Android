package com.example.ezroom.domain.usecase

import com.example.ezroom.core.AppError
import com.example.ezroom.core.Try
import com.example.ezroom.domain.model.Property
import com.example.ezroom.domain.model.Room
import com.example.ezroom.domain.repository.RoomRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

// Business Logic: Fetch All Rooms
class GetRoomsUseCase(private val repository: RoomRepository) {
    operator fun invoke(): Flow<Try<List<Room>>> = repository.getRooms()
        .map<List<Room>, Try<List<Room>>> { Try.Success(it) }
        .catch { emit(Try.Failure(AppError.Unknown(it))) }
}

// Business Logic: Fetch All Properties
class GetPropertiesUseCase(private val repository: RoomRepository) {
    operator fun invoke(): Flow<Try<List<Property>>> = repository.getProperties()
        .map<List<Property>, Try<List<Property>>> { Try.Success(it) }
        .catch { emit(Try.Failure(AppError.Unknown(it))) }
}

// Business Logic: Toggle Property Visibility
class TogglePropertyVisibilityUseCase(private val repository: RoomRepository) {
    suspend operator fun invoke(propertyId: String) = repository.togglePropertyVisibility(propertyId)
}

// Business Logic: Delete Property
class DeletePropertyUseCase(private val repository: RoomRepository) {
    suspend operator fun invoke(propertyId: String) = repository.deleteProperty(propertyId)
}

// Business Logic: Toggle Room Visibility
class ToggleRoomVisibilityUseCase(private val repository: RoomRepository) {
    suspend operator fun invoke(roomId: String) = repository.toggleRoomVisibility(roomId)
}

// Business Logic: Delete Room
class DeleteRoomUseCase(private val repository: RoomRepository) {
    suspend operator fun invoke(roomId: String) = repository.deleteRoom(roomId)
}

// Business Logic: Save Property
class SavePropertyUseCase(private val repository: RoomRepository) {
    suspend operator fun invoke(property: Property) = repository.saveProperty(property)
}

// Business Logic: Get Property By ID
class GetPropertyByIdUseCase(private val repository: RoomRepository) {
    suspend operator fun invoke(propertyId: String): Property? = repository.getPropertyById(propertyId)
}

// Business Logic: Submit Appeal for Removed Room
class SubmitAppealUseCase(private val repository: RoomRepository) {
    suspend operator fun invoke(roomId: String, appealText: String, images: List<String>) = 
        repository.submitAppeal(roomId, appealText, images)
}
