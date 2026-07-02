package com.example.ezroom.domain.repository

import com.example.ezroom.domain.model.FilterParams
import com.example.ezroom.domain.model.Room
import kotlinx.coroutines.flow.Flow

interface SearchRepository {
    fun searchRooms(params: FilterParams): Flow<List<Room>>
    fun getRoomTypes(): Flow<List<String>>
    fun getAmenities(): Flow<List<String>>
}
