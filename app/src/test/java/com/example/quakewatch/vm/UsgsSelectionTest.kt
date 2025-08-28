package com.example.quakewatch.vm

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.example.quakewatch.testing.MainDispatcherRule
import io.mockk.mockk
import org.junit.Rule
import org.junit.Test

// EDIT imports to match your packages:
import com.example.quakewatch.domain.model.Earthquake
import com.example.quakewatch.data.QuakeRepository
import com.example.quakewatch.ui.UsgsViewModel

class UsgsSelectionTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun select_then_clearSelection_updates_selected_flow() = kotlinx.coroutines.test.runTest {
        val repo = mockk<QuakeRepository>(relaxed = true)
        val vm = UsgsViewModel(repo)

        vm.selected.test {
            // initial
            assertThat(awaitItem()).isNull()

            val eq = mockk<Earthquake>(relaxed = true)
            vm.select(eq)
            assertThat(awaitItem()).isSameInstanceAs(eq)

            vm.clearSelection()
            assertThat(awaitItem()).isNull()

            cancelAndIgnoreRemainingEvents()
        }
    }
}
