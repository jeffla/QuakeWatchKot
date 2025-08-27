package com.example.quakewatch.data.remote

data class FeatureCollection(val features: List<Feature>)
data class Feature(
    val id: String,
    val properties: Properties,
    val geometry: Geometry
)

data class Properties(
    val mag: Double?,
    val place: String?,
    val time: Long,
    val url: String?,
    val detail: String?
)

data class Geometry(val coordinates: List<Double>) // [lon, lat, depth]
