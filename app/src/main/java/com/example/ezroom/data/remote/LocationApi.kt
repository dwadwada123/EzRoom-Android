package com.example.ezroom.data.remote

import com.example.ezroom.util.ApiConfig
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

data class Province(
    val name: String,
    val code: String,
    val wards: List<Ward> = emptyList(),
)

data class Ward(
    val name: String,
    val code: String,
)

// API for Vietnam provinces data
interface LocationApi {
    @GET("api/provinces")
    suspend fun getProvinces(): List<Province>

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
