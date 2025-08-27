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
    data class Success(val quakes: List<Earthquake>) : UiState
    data class Error(val message: String) : UiState
}

@HiltViewModel
class UsgsViewModel @Inject constructor(
    private val repository: QuakeRepository
) : ViewModel() {

    private val _state = MutableStateFlow<UiState>(UiState.Loading)
    val state: StateFlow<UiState> = _state

    private val _selected = MutableStateFlow<Earthquake?>(null)
    val selected: StateFlow<Earthquake?> = _selected

    init {
        refresh()
    }

    fun refresh(limit: Int = 100) {
        viewModelScope.launch {
            _state.value = UiState.Loading
            try {
                val quakes = repository.fetchRecent(limit)
                _state.value = UiState.Success(quakes)
            } catch (t: Throwable) {
                _state.value = UiState.Error(t.message ?: "Unknown error")
            }
        }
    }

    fun select(eq: Earthquake) {
        _selected.value = eq
    }

    fun clearSelection() {
        _selected.value = null
    }
}
