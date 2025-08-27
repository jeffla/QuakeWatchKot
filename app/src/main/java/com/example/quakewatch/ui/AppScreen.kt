// app/src/main/java/com/example/quakewatch/ui/AppScreen.kt
package com.example.quakewatch.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScreen(
    viewModel: UsgsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val refreshing = state is UiState.Loading

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("QuakeWatch") },
                actions = {
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(imageVector = Icons.Filled.Refresh, contentDescription = "Refresh")
                    }
                }
            )
        }
    ) { innerPadding ->
        PullToRefreshBox(
            isRefreshing = refreshing,
            onRefresh = { viewModel.refresh() },
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            when (val s = state) {
                UiState.Loading ->
                    Text("Loading…", modifier = Modifier.padding(16.dp))
                is UiState.Error ->
                    Text("Error: ${s.message}", modifier = Modifier.padding(16.dp))
                is UiState.Success -> {
                    if (s.quakes.isEmpty()) {
                        Text(
                            "No recent earthquakes found.",
                            modifier = Modifier.padding(16.dp)
                        )
                    } else {
                        LazyColumn {
                            items(s.quakes, key = { it.id }) { eq ->
                                EarthquakeRow(eq = eq)
                            }
                        }
                    }
                }
            }
        }
    }
}
