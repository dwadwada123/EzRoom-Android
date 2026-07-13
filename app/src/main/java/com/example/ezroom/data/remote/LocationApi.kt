package com.example.ezroom.data.remote

import com.example.ezroom.util.ApiConfig
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

// Data Transfer Object: Province
data class ProvinceDto(
    val name: String,
    val code: String,
    val wards: List<WardDto> = emptyList(),
)

// Data Transfer Object: Ward
data class WardDto(
    val name: String,
    val code: String,
)

// Remote Data Source: Location API
interface LocationApi {
    @GET("api/provinces")
    suspend fun getProvinces(): List<ProvinceDto>

    companion object {
        fun create(): LocationApi {
            return Retrofit.Builder()
                .baseUrl(ApiConfig.getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(LocationApi::class.java)
        }
    }
}
