package com.example.ezroom.domain.repository

import com.example.ezroom.domain.model.Province

// Domain Repository: Location
interface LocationRepository {
    suspend fun getProvinces(): List<Province>
}
