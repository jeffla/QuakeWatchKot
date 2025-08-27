package com.example.quakewatch.data.remote

import retrofit2.http.GET

interface UsgsApi {
    // Past day – all magnitudes
    @GET("summary/all_day.geojson")
    suspend fun getAllDay(): FeatureCollection
}
