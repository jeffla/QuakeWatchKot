// app/src/main/java/com/example/quakewatch/domain/model/Earthquake.kt
package com.example.quakewatch.domain.model

data class Earthquake(
    val id: String,
    val mag: Double?,
    val title: String,
    val timeMillis: Long,
    val lat: Double,
    val lon: Double,
    val depthKm: Double,
    // New optional fields for richer UI:
    val alert: String? = null,   // "green" | "yellow" | "orange" | "red" | null
    val tsunami: Boolean = false,
    val cdi: Double? = null,
    val mmi: Double? = null,
    val felt: Int? = null,
    val significance: Int? = null
)
