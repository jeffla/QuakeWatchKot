// app/src/main/java/com/example/quakewatch/data/QuakeRepositoryImpl.kt
package com.example.quakewatch.data

import com.example.quakewatch.domain.model.Earthquake
import com.example.quakewatch.network.UsgsApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuakeRepositoryImpl @Inject constructor(
    private val api: UsgsApi
) : QuakeRepository {

    override suspend fun fetchRecentCount(limit: Int): Int {
        return api.getRecent(limit = limit).features.size
    }

    override suspend fun fetchRecent(limit: Int): List<Earthquake> {
        val resp = api.getRecent(limit = limit)
        return resp.features.map { f ->
            Earthquake(
                id = f.id,
                mag = f.properties.mag,
                title = f.properties.place ?: "Unknown place",
                timeMillis = f.properties.time ?: 0L,
                lat = f.geometry?.coordinates?.getOrNull(1) ?: 0.0,
                lon = f.geometry?.coordinates?.getOrNull(0) ?: 0.0,
                depthKm = f.geometry?.coordinates?.getOrNull(2) ?: 0.0
            )
        }
    }
}
