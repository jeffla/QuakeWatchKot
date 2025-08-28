// app/src/main/java/com/example/quakewatch/ui/UsgsViewModel.kt
package com.example.quakewatch.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quakewatch.data.QuakeRepository
import com.example.quakewatch.domain.model.Earthquake
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed interface UiState {
    data object Loading : UiState
    data class Success(
        val quakes: List<Earthquake>,
        val refreshing: Boolean = false
    ) : UiState
    data class Error(val message: String) : UiState
}

@HiltViewModel
class UsgsViewModel @Inject constructor(
    private val repository: QuakeRepository
) : ViewModel() {

    private val _state = MutableStateFlow<UiState>(UiState.Loading)
    val state: StateFlow<UiState> = _state

    init {
        refresh(firstLoad = true)
    }

    fun refresh(limit: Int = 100, firstLoad: Boolean = false) {
        viewModelScope.launch {
            val prev = _state.value
            if (firstLoad || prev !is UiState.Success) {
                // Show full-screen loading only on app start / no cached data
                _state.value = UiState.Loading
            } else {
                // Keep current list visible and mark as refreshing
                _state.value = prev.copy(refreshing = true)
            }

            try {
                val quakes = repository.fetchRecent(limit)
                _state.value = UiState.Success(quakes = quakes, refreshing = false)
            } catch (t: Throwable) {
                val msg = t.message ?: "Unknown error"
                // If we had data, keep showing it and stop the spinner; otherwise show Error
                if (prev is UiState.Success) {
                    _state.value = prev.copy(refreshing = false)
                } else {
                    _state.value = UiState.Error(msg)
                }
            }
        }
    }

    // Selection for the native detail screen
    private val _selected = MutableStateFlow<Earthquake?>(null)
    val selected: StateFlow<Earthquake?> = _selected

    fun select(eq: Earthquake) {
        _selected.value = eq
    }

    fun clearSelection() {
        _selected.value = null
    }
}
