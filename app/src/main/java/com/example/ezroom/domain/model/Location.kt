package com.example.ezroom.domain.model

import com.google.gson.annotations.SerializedName

// Domain Model: Province
data class Province(
    @SerializedName("Name") val name: String,
    @SerializedName("Code") val code: String,
    @SerializedName("Wards") val wards: List<Ward> = emptyList(),
)

// Domain Model: Ward
data class Ward(
    @SerializedName("Name") val name: String,
    @SerializedName("Code") val code: String,
)
