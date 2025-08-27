// app/src/main/java/com/example/quakewatch/ui/EarthquakeDetailScreen.kt
package com.example.quakewatch.ui

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.quakewatch.domain.model.Earthquake
import com.example.quakewatch.ui.util.formatMillis
import com.example.quakewatch.ui.util.formatRelative

@Composable
private fun MagnitudeChipLarge(mag: Double?, modifier: Modifier = Modifier) {
    val m = mag ?: 0.0
    val container = when {
        m < 3.0 -> MaterialTheme.colorScheme.secondaryContainer
        m < 5.0 -> MaterialTheme.colorScheme.primaryContainer
        m < 7.0 -> MaterialTheme.colorScheme.tertiaryContainer
        else -> MaterialTheme.colorScheme.errorContainer
    }
    val onContainer = when {
        m < 3.0 -> MaterialTheme.colorScheme.onSecondaryContainer
        m < 5.0 -> MaterialTheme.colorScheme.onPrimaryContainer
        m < 7.0 -> MaterialTheme.colorScheme.onTertiaryContainer
        else -> MaterialTheme.colorScheme.onErrorContainer
    }

    Text(
        text = "M %.1f".format(m),
        color = onContainer,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.ExtraBold,
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(container)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    )
}

@Composable
fun EarthquakeDetailScreen(
    eq: Earthquake,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Column(modifier = modifier.padding(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth()) {
            MagnitudeChipLarge(eq.mag)
            Spacer(Modifier.width(12.dp))
            Column {
                Text(eq.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text(
                    "${formatMillis(eq.timeMillis)} • ${formatRelative(eq.timeMillis)}",
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }

        Spacer(Modifier.width(0.dp)) // just spacing helper

        Column(modifier = Modifier.padding(top = 16.dp)) {
            Text("Details", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
            Text("Latitude:  ${"%.4f".format(eq.lat)}", style = MaterialTheme.typography.bodyMedium)
            Text("Longitude: ${"%.4f".format(eq.lon)}", style = MaterialTheme.typography.bodyMedium)
            Text("Depth:     ${"%.1f".format(eq.depthKm)} km", style = MaterialTheme.typography.bodyMedium)
        }

        // Actions
        Row(modifier = Modifier.padding(top = 16.dp)) {
            Button(
                onClick = {
                    // Open native maps (geo URI)
                    val uri = Uri.parse("geo:${eq.lat},${eq.lon}?q=${eq.lat},${eq.lon} (${Uri.encode(eq.title)})")
                    val intent = Intent(Intent.ACTION_VIEW, uri)
                    try {
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        Toast.makeText(context, "No maps app available", Toast.LENGTH_SHORT).show()
                    }
                }
            ) { Text("Open in Maps") }

            Spacer(Modifier.width(12.dp))

            OutlinedButton(
                onClick = {
                    val share = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(
                            Intent.EXTRA_TEXT,
                            "Earthquake ${"%.1f".format(eq.mag ?: 0.0)} • ${eq.title}\n" +
                                    "Time: ${formatMillis(eq.timeMillis)} (${formatRelative(eq.timeMillis)})\n" +
                                    "Lat/Lon: ${"%.4f".format(eq.lat)}, ${"%.4f".format(eq.lon)} • Depth: ${"%.1f".format(eq.depthKm)} km"
                        )
                    }
                    context.startActivity(Intent.createChooser(share, "Share earthquake"))
                }
            ) { Text("Share") }
        }
    }
}
