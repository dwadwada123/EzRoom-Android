package com.example.ezroom.data.remote

import com.example.ezroom.util.ApiConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

/**
 * Data model for a Province (Tỉnh/Thành phố)
 * Simplified to 2-level hierarchy: Province -> Ward
 */
data class Province(
    val code: String,
    val name: String,
    val fullName: String,
    val codeName: String,
    val type: String,
    val administrativeUnitId: Int,
    val wards: List<Ward> = emptyList()
)

/**
 * Data model for a Ward (Phường/Xã/Khu vực)
 */
data class Ward(
    val code: String,
    val name: String,
    val fullName: String,
    val codeName: String,
    val type: String,
    val administrativeUnitId: Int
)

/**
 * API interface for location-related services.
 * Implements Observability via HttpLoggingInterceptor.
 */
interface LocationApi {
    @GET("api/provinces")
    suspend fun getProvinces(): List<Province>

    companion object {
        fun create(): LocationApi {
            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
            
            val client = OkHttpClient.Builder()
                .addInterceptor(logging)
                .build()

            return Retrofit.Builder()
                .baseUrl(ApiConfig.getBaseUrl())
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(LocationApi::class.java)
        }
    }
}
