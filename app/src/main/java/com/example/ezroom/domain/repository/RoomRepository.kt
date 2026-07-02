package com.example.ezroom.domain.repository

import com.example.ezroom.domain.model.Property
import com.example.ezroom.domain.model.Room
import kotlinx.coroutines.flow.Flow

interface RoomRepository {
    fun getRooms(): Flow<List<Room>>
    fun getProperties(): Flow<List<Property>>
}
