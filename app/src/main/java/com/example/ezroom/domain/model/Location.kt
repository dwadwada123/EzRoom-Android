package com.example.ezroom.domain.model

// Domain Model: Province
data class Province(
    val name: String,
    val code: String,
    val wards: List<Ward> = emptyList(),
)

// Domain Model: Ward
data class Ward(
    val name: String,
    val code: String,
)
