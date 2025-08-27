package com.example.quakewatch.domain.usecase

import com.example.quakewatch.data.QuakeRepository
import javax.inject.Inject

class GetQuakesUseCase @Inject constructor(
    private val repo: QuakeRepository
) {
    // Minimal happy-path: just return the recent count
    suspend operator fun invoke(limit: Int = 5): Int {
        return repo.fetchRecentCount(limit)
    }
}
