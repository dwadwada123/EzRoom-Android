package com.example.ezroom.data.repository

import com.example.ezroom.data.remote.LocationApi
import com.example.ezroom.data.remote.ProvinceDto
import com.example.ezroom.data.remote.WardDto
import com.example.ezroom.domain.model.Province
import com.example.ezroom.domain.model.Ward
import com.example.ezroom.domain.repository.LocationRepository

// Data Layer: Location Repository Implementation
class LocationRepositoryImpl(
    private val api: LocationApi = LocationApi.create()
) : LocationRepository {

    override suspend fun getProvinces(): List<Province> {
        return api.getProvinces().map { it.toDomain() }
    }
}

// Data Mapping: Province DTO to Domain
fun ProvinceDto.toDomain() = Province(
    name = name,
    code = code,
    wards = wards.map { it.toDomain() }
)

// Data Mapping: Ward DTO to Domain
fun WardDto.toDomain() = Ward(
    name = name,
    code = code
)
