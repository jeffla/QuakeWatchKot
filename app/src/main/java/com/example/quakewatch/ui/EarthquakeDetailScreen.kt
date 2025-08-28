// app/src/main/java/com/example/quakewatch/ui/EarthquakeDetailScreen.kt
package com.example.quakewatch.ui

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
private fun Pill(text: String, kind: String, modifier: Modifier = Modifier) {
    val (container, onContainer) = when (kind) {
        "alert-green" -> MaterialTheme.colorScheme.secondaryContainer to MaterialTheme.colorScheme.onSecondaryContainer
        "alert-yellow" -> MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer
        "alert-orange" -> MaterialTheme.colorScheme.tertiaryContainer to MaterialTheme.colorScheme.onTertiaryContainer
        "alert-red" -> MaterialTheme.colorScheme.errorContainer to MaterialTheme.colorScheme.onErrorContainer
        "tsunami" -> MaterialTheme.colorScheme.errorContainer to MaterialTheme.colorScheme.onErrorContainer
        "intensity" -> MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer
        else -> MaterialTheme.colorScheme.secondaryContainer to MaterialTheme.colorScheme.onSecondaryContainer
    }

    Text(
        text = text,
        color = onContainer,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Bold,
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(container)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    )
}

@Composable
private fun AlertChip(alert: String?) {
    val a = alert?.lowercase() ?: return
    when (a) {
        "green" -> Pill("Alert: Green", "alert-green")
        "yellow" -> Pill("Alert: Yellow", "alert-yellow")
        "orange" -> Pill("Alert: Orange", "alert-orange")
        "red" -> Pill("Alert: Red", "alert-red")
    }
}

@Composable
private fun IntensityChip(mmi: Double?, cdi: Double?) {
    when {
        mmi != null -> Pill("MMI %.1f".format(mmi), "intensity")
        cdi != null -> Pill("CDI %.1f".format(cdi), "intensity")
    }
}

@Composable
private fun TsunamiChip(tsunami: Boolean) {
    if (tsunami) Pill("Tsunami", "tsunami")
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

        Spacer(Modifier.height(12.dp))
        Row {
            AlertChip(eq.alert)
            if (eq.alert != null) Spacer(Modifier.width(8.dp))
            IntensityChip(eq.mmi, eq.cdi)
            if ((eq.mmi != null || eq.cdi != null) && eq.tsunami) Spacer(Modifier.width(8.dp))
            TsunamiChip(eq.tsunami)
        }

        Spacer(Modifier.height(16.dp))
        Column {
            Text("Details", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
            Text("Latitude:  ${"%.4f".format(eq.lat)}", style = MaterialTheme.typography.bodyMedium)
            Text("Longitude: ${"%.4f".format(eq.lon)}", style = MaterialTheme.typography.bodyMedium)
            Text("Depth:     ${"%.1f".format(eq.depthKm)} km", style = MaterialTheme.typography.bodyMedium)
            eq.felt?.let { Text("Felt reports: $it", style = MaterialTheme.typography.bodyMedium) }
            eq.significance?.let { Text("Significance: $it", style = MaterialTheme.typography.bodyMedium) }
        }

        Spacer(Modifier.height(16.dp))
        Row {
            Button(
                onClick = {
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
                                    "Lat/Lon: ${"%.4f".format(eq.lat)}, ${"%.4f".format(eq.lon)} • Depth: ${"%.1f".format(eq.depthKm)} km" +
                                    (if (eq.tsunami) "\nTsunami: Possible" else "") +
                                    (eq.alert?.let { "\nAlert: ${it.replaceFirstChar(Char::titlecase)}" } ?: "") +
                                    (eq.mmi?.let { "\nMMI: ${"%.1f".format(it)}" } ?: (eq.cdi?.let { "\nCDI: ${"%.1f".format(it)}" } ?: ""))
                        )
                    }
                    context.startActivity(Intent.createChooser(share, "Share earthquake"))
                }
            ) { Text("Share") }
        }
    }
}
