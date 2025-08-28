package com.example.quakewatch.vm

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.example.quakewatch.testing.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.mockk
import org.junit.Rule
import org.junit.Test
import java.io.IOException

// EDIT imports to match your packages:
import com.example.quakewatch.data.QuakeRepository
import com.example.quakewatch.ui.UsgsViewModel
import com.example.quakewatch.ui.UiState

class UsgsRetryTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun firstLoad_error_then_retry_success() = kotlinx.coroutines.test.runTest {
        val repo = mockk<QuakeRepository>()
        coEvery { repo.fetchRecent() } throws IOException("boom") andThen emptyList()

        val vm = UsgsViewModel(repo)

        vm.state.test {
            // First attempt (error)
            vm.refresh(firstLoad = true)
            assertThat(awaitItem()).isInstanceOf(UiState.Loading::class.java)
            assertThat(awaitItem()).isInstanceOf(UiState.Error::class.java)

            // Retry
            vm.refresh(firstLoad = true)
            // tolerate multiple Loading emissions if any
            var next = awaitItem()
            while (next is UiState.Loading) next = awaitItem()
            assertThat(next).isInstanceOf(UiState.Success::class.java)

            cancelAndIgnoreRemainingEvents()
        }
    }
}
