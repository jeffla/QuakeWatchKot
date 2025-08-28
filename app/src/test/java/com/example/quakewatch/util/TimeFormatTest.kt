package com.example.quakewatch.ui.util

import com.google.common.truth.Truth.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.util.TimeZone

class TimeFormatTest {

    private var originalTz: TimeZone? = null

    @Before
    fun setUp() {
        // Make formatMillis deterministic regardless of the machine running the tests.
        originalTz = TimeZone.getDefault()
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
    }

    @After
    fun tearDown() {
        originalTz?.let { TimeZone.setDefault(it) }
    }

    // ---------- formatMillis ----------

    @Test
    fun formatMillis_epochZero_utc() {
        // 1970-01-01 00:00:00 UTC
        val out = formatMillis(0L)
        assertThat(out).isEqualTo("1970-01-01 00:00")
    }

    @Test
    fun formatMillis_knownTimestamp_utc() {
        // 2024-01-01 00:00:00 UTC
        val ts = 1_704_067_200_000L
        val out = formatMillis(ts)
        assertThat(out).isEqualTo("2024-01-01 00:00")
    }

    // ---------- formatRelative ----------

    @Test
    fun formatRelative_justNow_boundaries() {
        val now = 1_000_000L
        // exact now
        assertThat(formatRelative(ms = now, nowMs = now)).isEqualTo("just now")
        // 4.999s diff -> still "just now" (s = 4)
        assertThat(formatRelative(ms = now - 4_999, nowMs = now)).isEqualTo("just now")
        // 5.000s -> 5s ago
        assertThat(formatRelative(ms = now - 5_000, nowMs = now)).isEqualTo("5s ago")
        // 5.000s in the future
        assertThat(formatRelative(ms = now + 5_000, nowMs = now)).isEqualTo("5s from now")
    }

    @Test
    fun formatRelative_seconds() {
        val now = 10_000_000L
        assertThat(formatRelative(ms = now - 42_000, nowMs = now)).isEqualTo("42s ago")
        assertThat(formatRelative(ms = now + 42_000, nowMs = now)).isEqualTo("42s from now")
    }

    @Test
    fun formatRelative_minutes() {
        val now = 20_000_000L
        // 61s -> 1m
        assertThat(formatRelative(ms = now - 61_000, nowMs = now)).isEqualTo("1m ago")
        // 59m 59s -> 59m (floor to minutes)
        assertThat(formatRelative(ms = now - 3_599_000, nowMs = now)).isEqualTo("59m ago")
        // future
        assertThat(formatRelative(ms = now + 2_800_000, nowMs = now)).isEqualTo("46m from now")
    }

    @Test
    fun formatRelative_hours() {
        val now = 30_000_000L
        assertThat(formatRelative(ms = now - 7_200_000, nowMs = now)).isEqualTo("2h ago")
        assertThat(formatRelative(ms = now + 10_800_000, nowMs = now)).isEqualTo("3h from now")
    }

    @Test
    fun formatRelative_days() {
        val now = 40_000_000L
        val oneDayMs = 86_400_000L
        assertThat(formatRelative(ms = now - 3 * oneDayMs, nowMs = now)).isEqualTo("3d ago")
        assertThat(formatRelative(ms = now + 5 * oneDayMs, nowMs = now)).isEqualTo("5d from now")
    }
}
