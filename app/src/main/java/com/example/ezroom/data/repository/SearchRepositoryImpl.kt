package com.example.ezroom.data.repository

import com.example.ezroom.data.model.MockData
import com.example.ezroom.domain.model.FilterParams
import com.example.ezroom.domain.model.Room
import com.example.ezroom.domain.repository.SearchRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SearchRepositoryImpl : SearchRepository {
    override fun searchRooms(params: FilterParams): Flow<List<Room>> = flow {
        // Mock filtering logic
        emit(MockData.rooms)
    }

    override fun getRoomTypes(): Flow<List<String>> = flow {
        emit(listOf("Dãy / Tòa nhà", "Phòng lẻ"))
    }

    override fun getAmenities(): Flow<List<String>> = flow {
        try {
            val api = com.example.ezroom.data.remote.NetworkClient.createService<com.example.ezroom.data.remote.AmenityApi>()
            val amenities = api.getAmenities().map { it.name }
            if (amenities.isNotEmpty()) {
                emit(amenities)
            } else {
                emit(listOf("WiFi", "Máy giặt", "Điều hòa", "Tủ lạnh", "Kệ bếp", "Giờ giấc tự do", "Bảo vệ 24/7", "Thang máy"))
            }
        } catch (e: Exception) {
            emit(listOf("WiFi", "Máy giặt", "Điều hòa", "Tủ lạnh", "Kệ bếp", "Giờ giấc tự do", "Bảo vệ 24/7", "Thang máy"))
        }
    }
}
