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
    val time: Long?
)

data class Geometry(
    val coordinates: List<Double> // [lon, lat, depth]
)
