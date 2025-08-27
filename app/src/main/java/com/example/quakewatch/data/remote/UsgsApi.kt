// app/src/main/java/com/example/quakewatch/network/UsgsApi.kt
package com.example.quakewatch.network

import com.example.quakewatch.data.UsgsResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * USGS Earthquake API
 * Docs: https://earthquake.usgs.gov/fdsnws/event/1/
 */
interface UsgsApi {

    /**
     * Query recent earthquakes.
     *
     * Common examples:
     *  - Most recent (default): order by time, 50 results.
     *  - Limit results by passing [limit].
     *  - Time window by passing ISO8601 strings in [starttime]/[endtime]
     *    e.g., "2025-08-26T00:00:00" (UTC assumed if no zone).
     */
    @GET("fdsnws/event/1/query")
    suspend fun getRecent(
        @Query("format") format: String = "geojson",
        @Query("orderby") orderby: String = "time",
        @Query("limit") limit: Int = 50,
        @Query("starttime") starttime: String? = null,
        @Query("endtime") endtime: String? = null
    ): UsgsResponse
}
