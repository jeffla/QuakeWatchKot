// app/src/main/java/com/example/quakewatch/ui/EarthquakeRow.kt
package com.example.quakewatch.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.quakewatch.domain.model.Earthquake
import com.example.quakewatch.ui.util.formatMillis
import com.example.quakewatch.ui.util.formatRelative

@Composable
private fun MagnitudeChip(mag: Double?, modifier: Modifier = Modifier) {
    val m = mag ?: 0.0
    val cs = MaterialTheme.colorScheme
    val container = when {
        m < 3.0 -> cs.secondaryContainer
        m < 5.0 -> cs.primaryContainer
        m < 7.0 -> cs.tertiaryContainer
        else -> cs.errorContainer
    }
    val onContainer = when {
        m < 3.0 -> cs.onSecondaryContainer
        m < 5.0 -> cs.onPrimaryContainer
        m < 7.0 -> cs.onTertiaryContainer
        else -> cs.onErrorContainer
    }

    Text(
        text = "M %.1f".format(m),
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
private fun SeverityChip(mag: Double?, modifier: Modifier = Modifier) {
    val m = mag ?: 0.0
    val cs = MaterialTheme.colorScheme
    val (label, container, onContainer) = when {
        m < 3.0 -> Triple("Minor", cs.secondaryContainer, cs.onSecondaryContainer)
        m < 4.0 -> Triple("Light", cs.primaryContainer, cs.onPrimaryContainer)
        m < 5.0 -> Triple("Moderate", cs.primaryContainer, cs.onPrimaryContainer)
        m < 6.0 -> Triple("Strong", cs.tertiaryContainer, cs.onTertiaryContainer)
        m < 7.0 -> Triple("Major", cs.errorContainer, cs.onErrorContainer)
        else -> Triple("Great", cs.errorContainer, cs.onErrorContainer)
    }

    Text(
        text = label,
        color = onContainer,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.SemiBold,
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(container)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    )
}

@Composable
fun EarthquakeRow(
    eq: Earthquake,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val rowModifier = if (onClick != null) modifier.clickable(onClick = onClick) else modifier

    Column(
        modifier = rowModifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // Line 1: chips only (no text next to them)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MagnitudeChip(eq.mag)
            SeverityChip(eq.mag)
        }

        // Line 2: title
        Text(
            text = eq.title,
            style = MaterialTheme.typography.bodyLarge,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 6.dp)
        )

        // Line 3: timestamp
        Text(
            text = "${formatMillis(eq.timeMillis)} • ${formatRelative(eq.timeMillis)}",
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(top = 2.dp)
        )

        Divider(modifier = Modifier.padding(top = 12.dp))
    }
}
