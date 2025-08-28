// app/src/main/java/com/example/quakewatch/ui/EarthquakeMap.kt
package com.example.quakewatch.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.quakewatch.R
import com.example.quakewatch.domain.model.Earthquake
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun EarthquakeMap(
    eq: Earthquake,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val mapsKey = context.getString(R.string.google_maps_key) // from resValue / local.properties

    val center = LatLng(eq.lat, eq.lon)
    val cameraState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(center, zoomFor(eq.mag))
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(220.dp)
            .clip(RoundedCornerShape(12.dp))
    ) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraState,
            uiSettings = MapUiSettings(
                zoomControlsEnabled = false,
                mapToolbarEnabled = false,
                compassEnabled = true
            )
        ) {
            Marker(
                state = MarkerState(position = center),
                title = "M %.1f".format(eq.mag ?: 0.0),
                snippet = eq.title
            )
        }

        // If no key is configured, show a friendly hint (tiles may appear gray).
        if (mapsKey.isBlank()) {
            Text(
                text = "Add MAPS_API_KEY to local.properties to enable map tiles",
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.85f))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            )
        }
    }
}

private fun zoomFor(mag: Double?): Float = when ((mag ?: 0.0)) {
    in 0.0..2.9 -> 6f
    in 3.0..3.9 -> 6.5f
    in 4.0..4.9 -> 7f
    in 5.0..5.9 -> 7.5f
    in 6.0..6.9 -> 8f
    else -> 8.5f
}
