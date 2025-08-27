package com.example.quakewatch.domain.model

data class Earthquake(
    val id: String,
    val mag: Double?,
    val title: String,
    val timeMillis: Long,
    val lat: Double,
    val lon: Double,
    val depthKm: Double
)
