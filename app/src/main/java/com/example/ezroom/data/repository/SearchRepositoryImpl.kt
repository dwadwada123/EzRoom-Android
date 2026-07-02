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
        emit(listOf("Phòng trọ", "Chung cư mini", "Căn hộ dịch vụ", "Ở ghép", "Nhà nguyên căn"))
    }

    override fun getAmenities(): Flow<List<String>> = flow {
        emit(listOf("WiFi", "Máy giặt", "Điều hòa", "Tủ lạnh", "Kệ bếp", "Giờ giấc tự do", "Bảo vệ 24/7", "Thang máy"))
    }
}
