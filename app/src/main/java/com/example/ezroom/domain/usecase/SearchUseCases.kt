package com.example.ezroom.domain.usecase

import com.example.ezroom.domain.model.FilterParams
import com.example.ezroom.domain.repository.SearchRepository
import kotlinx.coroutines.flow.Flow

class SearchRoomsUseCase(private val repository: SearchRepository) {
    operator fun invoke(params: FilterParams) = repository.searchRooms(params)
}

class GetSearchMetadataUseCase(private val repository: SearchRepository) {
    fun getRoomTypes(): Flow<List<String>> = repository.getRoomTypes()
    fun getAmenities(): Flow<List<String>> = repository.getAmenities()
}
