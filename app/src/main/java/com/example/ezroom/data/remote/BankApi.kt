package com.example.ezroom.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

// Data Transfer Object: VietQR Bank Response
data class BankResponse(
    val code: String,
    val desc: String,
    val data: List<BankDto>
)

// Data Transfer Object: Bank Details
data class BankDto(
    val id: Int,
    val name: String,
    val code: String,
    val bin: String,
    val logo: String,
    val shortName: String? = null
)

// Remote Data Source: VietQR Banks API
interface BankApi {
    @GET("banks")
    suspend fun getBanks(): BankResponse

    companion object {
        private const val BASE_URL = "https://api.vietqr.io/v2/"

        fun create(): BankApi {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(BankApi::class.java)
        }
    }
}
