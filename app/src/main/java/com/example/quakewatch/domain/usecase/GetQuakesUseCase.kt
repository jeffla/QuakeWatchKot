package com.example.quakewatch.domain.usecase

import com.example.quakewatch.data.QuakeRepository
import com.example.quakewatch.domain.model.Earthquake
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetQuakesUseCase @Inject constructor(
    private val repo: QuakeRepository
) {
    operator fun invoke(): Flow<List<Earthquake>> = repo.quakes()
}
