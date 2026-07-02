package com.example.ezroom.data.remote

import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LocationApiTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var api: LocationApi

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        api = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(LocationApi::class.java)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `getProvinces should return list of provinces with wards directly`() = runBlocking {
        // Given: 2-level response (Province -> Ward)
        val jsonResponse = """
            [
              {
                "code": "01",
                "name": "Thành phố Hà Nội",
                "fullName": "Thành phố Hà Nội",
                "codeName": "thanh_pho_ha_noi",
                "type": "thanh_pho_trung_uong",
                "administrativeUnitId": 1,
                "wards": [
                  {
                    "code": "00001",
                    "name": "Phường Phúc Xá",
                    "fullName": "Phường Phúc Xá",
                    "codeName": "phuong_phuc_xa",
                    "type": "phuong",
                    "administrativeUnitId": 11
                  }
                ]
              }
            ]
        """.trimIndent()
        
        mockWebServer.enqueue(MockResponse().setBody(jsonResponse).setResponseCode(200))

        // When
        val provinces = api.getProvinces()

        // Then
        assertEquals(1, provinces.size)
        val hanoi = provinces[0]
        assertEquals("01", hanoi.code)
        assertEquals(1, hanoi.wards.size)
        
        val phucxa = hanoi.wards[0]
        assertEquals("00001", phucxa.code)
        assertEquals("Phường Phúc Xá", phucxa.name)
    }
}
