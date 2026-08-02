package com.example.ezroom.data.repository

import com.example.ezroom.EzRoomApplication
import com.example.ezroom.domain.model.Province
import com.example.ezroom.domain.repository.LocationRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStreamReader

class LocationRepositoryImpl : LocationRepository {

    override suspend fun getProvinces(): List<Province> {
        return try {
            val context = EzRoomApplication.getContext()
            val inputStream = context.assets.open("data/vietnam_provinces.json")
            val reader = InputStreamReader(inputStream)
            val listType = object : TypeToken<List<Province>>() {}.type
            val provinces: List<Province> = Gson().fromJson(reader, listType)
            reader.close()
            provinces
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}
