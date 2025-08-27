package com.example.quakewatch.data

import com.example.quakewatch.domain.model.Earthquake
import kotlinx.coroutines.flow.Flow

interface QuakeRepository {
    fun quakes(): Flow<List<Earthquake>>
}
