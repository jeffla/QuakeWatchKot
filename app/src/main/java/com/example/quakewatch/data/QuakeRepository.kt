// app/src/main/java/com/example/quakewatch/data/QuakeRepository.kt
package com.example.quakewatch.data

import com.example.quakewatch.domain.model.Earthquake

interface QuakeRepository {
    suspend fun fetchRecentCount(limit: Int = 1): Int
    suspend fun fetchRecent(limit: Int = 100): List<Earthquake>
}
