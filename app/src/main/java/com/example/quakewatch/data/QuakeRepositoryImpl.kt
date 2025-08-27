package com.example.quakewatch.data

import com.example.quakewatch.data.remote.UsgsApi
import com.example.quakewatch.domain.model.Earthquake
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class QuakeRepositoryImpl @Inject constructor(
    private val api: UsgsApi
) : QuakeRepository {

    override fun quakes(): Flow<List<Earthquake>> = flow {
        val fc = api.getAllDay()
        val mapped = fc.features.mapNotNull { f ->
            val coords = f.geometry.coordinates
            if (coords.size >= 3) {
                Earthquake(
                    id = f.id,
                    mag = f.properties.mag,
                    title = f.properties.place ?: "Unknown location",
                    timeMillis = f.properties.time,
                    lat = coords[1],
                    lon = coords[0],
                    depthKm = coords[2]
                )
            } else null
        }
        emit(mapped.sortedByDescending { it.timeMillis })
    }
}
