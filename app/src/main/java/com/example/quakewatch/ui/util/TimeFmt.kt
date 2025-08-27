package com.example.quakewatch.ui.util

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.abs

private val dateFmt: DateTimeFormatter =
    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").withZone(ZoneId.systemDefault())

/** Formats epoch millis as local date/time like `2025-08-27 01:23`. */
fun formatMillis(ms: Long): String = dateFmt.format(Instant.ofEpochMilli(ms))

/** Formats epoch millis as a compact relative string like `just now`, `12m ago`, `3h ago`, `2d ago`. */
fun formatRelative(ms: Long, nowMs: Long = System.currentTimeMillis()): String {
    val diff = nowMs - ms
    val s = abs(diff) / 1000
    return when {
        s < 5 -> "just now"
        s < 60 -> "${s}s ${if (diff >= 0) "ago" else "from now"}"
        s < 3600 -> "${s / 60}m ${if (diff >= 0) "ago" else "from now"}"
        s < 86400 -> "${s / 3600}h ${if (diff >= 0) "ago" else "from now"}"
        else -> "${s / 86400}d ${if (diff >= 0) "ago" else "from now"}"
    }
}
