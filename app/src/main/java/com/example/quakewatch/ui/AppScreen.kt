// app/src/main/java/com/example/quakewatch/ui/AppScreen.kt
package com.example.quakewatch.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScreen(
    viewModel: UsgsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val selected by viewModel.selected.collectAsState()
    val refreshing = state is UiState.Loading

    // Local (in-memory) min-mag filter; shown only on the list screen
    val (minMag, setMinMag) = remember { mutableStateOf<Double?>(null) }

    Scaffold(
        topBar = {
            // Tiny shadow/elevation under the app bar
            Surface(shadowElevation = 3.dp, tonalElevation = 3.dp) {
                TopAppBar(
                    title = {
                        Column {
                            Text(if (selected == null) "QuakeWatch" else "Earthquake Details")
                            if (selected == null) {
                                Spacer(Modifier.height(4.dp))
                                FiltersRow(
                                    minMag = minMag,
                                    onSelect = setMinMag
                                )
                            }
                        }
                    },
                    navigationIcon = {
                        if (selected != null) {
                            IconButton(onClick = { viewModel.clearSelection() }) {
                                Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                            }
                        }
                    },
                    actions = {
                        if (selected == null) {
                            IconButton(onClick = { viewModel.refresh() }) {
                                Icon(imageVector = Icons.Filled.Refresh, contentDescription = "Refresh")
                            }
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        PullToRefreshBox(
            isRefreshing = refreshing && selected == null,
            onRefresh = { viewModel.refresh() },
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            when {
                selected != null -> {
                    EarthquakeDetailScreen(eq = selected!!)
                }
                state is UiState.Loading -> {
                    Text("Loading…", modifier = Modifier.padding(16.dp))
                }
                state is UiState.Error -> {
                    val msg = (state as UiState.Error).message
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Error: $msg")
                        Spacer(Modifier.height(8.dp))
                        Button(onClick = { viewModel.refresh() }) {
                            Text("Retry")
                        }
                    }
                }
                state is UiState.Success -> {
                    val quakes = (state as UiState.Success).quakes
                    val shown = quakes.filter { (it.mag ?: 0.0) >= (minMag ?: 0.0) }

                    if (shown.isEmpty()) {
                        Text("No recent earthquakes found.", modifier = Modifier.padding(16.dp))
                    } else {
                        LazyColumn {
                            items(shown, key = { it.id }) { eq ->
                                EarthquakeRow(eq = eq, onClick = { viewModel.select(eq) })
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FiltersRow(
    minMag: Double?,
    onSelect: (Double?) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val colors = FilterChipDefaults.filterChipColors()

        FilterChip(
            selected = (minMag == null),
            onClick = { onSelect(null) },
            label = { Text("All") },
            colors = colors
        )
        FilterChip(
            selected = (minMag == 3.0),
            onClick = { onSelect(3.0) },
            label = { Text("3+") },
            colors = colors
        )
        FilterChip(
            selected = (minMag == 4.0),
            onClick = { onSelect(4.0) },
            label = { Text("4+") },
            colors = colors
        )
        FilterChip(
            selected = (minMag == 5.0),
            onClick = { onSelect(5.0) },
            label = { Text("5+") },
            colors = colors
        )
    }
}
