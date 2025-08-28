package com.example.quakewatch.vm

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.example.quakewatch.testing.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import java.io.IOException
import kotlin.collections.emptyList

import com.example.quakewatch.network.UsgsApi
import com.example.quakewatch.data.QuakeRepositoryImpl
import com.example.quakewatch.domain.model.Earthquake
import com.example.quakewatch.data.QuakeRepository
import com.example.quakewatch.ui.UsgsViewModel
import com.example.quakewatch.ui.UiState
// ===============================================================

class UsgsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun refresh_firstLoad_emits_Loading_then_Success_emptyList() = runTest {
        // Arrange
        val repo = mockk<QuakeRepository>()
        coEvery { repo.fetchRecent() } returns emptyList()

        // If your VM takes other deps, add them here.
        val vm = UsgsViewModel(repo)

        // Act + Assert
        vm.state.test {
            vm.refresh(firstLoad = true)

            // 1) Loading
            val loading = awaitItem()
            assertThat(loading).isInstanceOf(UiState.Loading::class.java)

            // 2) Success (with quakes empty, refreshing false)
            val success = awaitItem()
            assertThat(success).isInstanceOf(UiState.Success::class.java)
            val s = success as UiState.Success
            assertThat(s.quakes).isEmpty()
            assertThat(s.refreshing).isFalse()

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun refresh_firstLoad_emits_Loading_then_Error() = runTest {
        // Arrange
        val repo = mockk<QuakeRepository>()
        coEvery { repo.fetchRecent() } throws IOException("boom")

        val vm = UsgsViewModel(repo)

        // Act + Assert
        vm.state.test {
            vm.refresh(firstLoad = true)

            // 1) Loading
            val loading = awaitItem()
            assertThat(loading).isInstanceOf(UiState.Loading::class.java)

            // 2) Error
            val error = awaitItem()
            assertThat(error).isInstanceOf(UiState.Error::class.java)
            val e = error as UiState.Error
            // message text may be mapped; just assert non-empty
            assertThat(e.message).isNotEmpty()

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun refresh_onSuccess_emits_either_Loading_then_Success_or_Refreshing_true_then_false() = runTest {
        // Arrange: first call brings us to Success, second call is the pull-to-refresh
        val repo = mockk<QuakeRepository>()
        coEvery { repo.fetchRecent() } returnsMany listOf(
            emptyList(), // initial firstLoad
            emptyList()  // refresh()
        )

        val vm = UsgsViewModel(repo)

        vm.state.test {
            // Drive initial load to Success
            vm.refresh(firstLoad = true)

            // We expect at least one Loading then a Success; tolerate extra emissions
            var next = awaitItem()
            while (next is UiState.Loading) {
                next = awaitItem()
            }
            val firstSuccess = next as UiState.Success
            assertThat(firstSuccess.refreshing).isFalse()

            // Trigger pull-to-refresh (no firstLoad)
            vm.refresh()

            // Now tolerate either pattern:
            // 1) Loading -> Success
            // 2) Success(refreshing=true) -> Success(refreshing=false)
            next = awaitItem()
            when (next) {
                is UiState.Loading -> {
                    // Finish with Success
                    var after = awaitItem()
                    while (after is UiState.Loading) {
                        after = awaitItem()
                    }
                    val refreshed = after as UiState.Success
                    assertThat(refreshed.refreshing).isFalse()
                }
                is UiState.Success -> {
                    // If VM uses polish, it may flip refreshing true -> false
                    val maybeRefreshing = next as UiState.Success
                    if (maybeRefreshing.refreshing) {
                        val final = awaitItem() as UiState.Success
                        assertThat(final.refreshing).isFalse()
                    } else {
                        // Some VMs just emit a single Success with refreshing=false
                        assertThat(maybeRefreshing.refreshing).isFalse()
                    }
                }
                else -> error("Expected Loading or Success after refresh(), got $next")
            }

            cancelAndIgnoreRemainingEvents()
        }
    }
}
