// app/src/main/java/com/example/quakewatch/ui/EarthquakeRow.kt
package com.example.quakewatch.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.quakewatch.domain.model.Earthquake
import com.example.quakewatch.ui.util.formatMillis
import com.example.quakewatch.ui.util.formatRelative
import androidx.compose.foundation.shape.RoundedCornerShape

@Composable
private fun MagnitudeChip(mag: Double?, modifier: Modifier = Modifier) {
    val m = mag ?: 0.0
    val containerColor = when {
        m < 3.0 -> MaterialTheme.colorScheme.secondaryContainer
        m < 5.0 -> MaterialTheme.colorScheme.primaryContainer
        m < 7.0 -> MaterialTheme.colorScheme.tertiaryContainer
        else -> MaterialTheme.colorScheme.errorContainer
    }
    val contentColor = when {
        m < 3.0 -> MaterialTheme.colorScheme.onSecondaryContainer
        m < 5.0 -> MaterialTheme.colorScheme.onPrimaryContainer
        m < 7.0 -> MaterialTheme.colorScheme.onTertiaryContainer
        else -> MaterialTheme.colorScheme.onErrorContainer
    }

    Text(
        text = "M %.1f".format(m),
        color = contentColor,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Bold,
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(containerColor)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    )
}

@Composable
fun EarthquakeRow(
    eq: Earthquake,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val rowModifier = if (onClick != null) {
        modifier.clickable(onClick = onClick)
    } else {
        modifier
    }

    Column(modifier = rowModifier.padding(16.dp)) {
        Row {
            MagnitudeChip(eq.mag)
            Spacer(Modifier.width(12.dp))
            Column {
                Text(
                    text = eq.title,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "${formatMillis(eq.timeMillis)} • ${formatRelative(eq.timeMillis)}",
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
    Divider()
}
