// app/src/main/java/com/example/quakewatch/data/UsgsModels.kt
package com.example.quakewatch.data

data class UsgsResponse(
    val metadata: Metadata?,
    val features: List<Feature> = emptyList()
)

data class Metadata(
    val generated: Long?,
    val title: String?
)

data class Feature(
    val id: String,
    val properties: Properties,
    val geometry: Geometry?
)

data class Properties(
    val mag: Double?,
    val place: String?,
    val time: Long?,
    // Extras we’ll use for native detail UI:
    val alert: String?,   // "green" | "yellow" | "orange" | "red" | null
    val tsunami: Int?,    // 1 or 0
    val cdi: Double?,     // DYFI community intensity
    val mmi: Double?,     // maximum instrumental intensity
    val felt: Int?,       // number of felt reports
    val sig: Int?         // significance (0-1000)
)

data class Geometry(
    /** [lon, lat, depthKm] */
    val coordinates: List<Double>
)
