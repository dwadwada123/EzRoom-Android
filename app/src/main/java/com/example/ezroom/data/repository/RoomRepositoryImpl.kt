package com.example.ezroom.data.repository

import com.example.ezroom.data.model.MockData
import com.example.ezroom.domain.model.Property
import com.example.ezroom.domain.model.Room
import com.example.ezroom.domain.repository.RoomRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class RoomRepositoryImpl : RoomRepository {
    override fun getRooms(): Flow<List<Room>> = flow {
        emit(MockData.rooms)
    }

    override fun getProperties(): Flow<List<Property>> = flow {
        emit(MockData.properties)
    }
}
