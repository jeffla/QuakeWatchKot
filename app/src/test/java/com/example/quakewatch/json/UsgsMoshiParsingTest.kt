package com.example.quakewatch.json

import com.google.common.truth.Truth.assertThat
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okio.buffer
import okio.source
import org.junit.Test
import java.io.InputStream

// NOTE: No @JsonClass(generateAdapter = true) anywhere.
// Reflection adapters handle the mapping without codegen.
class UsgsMoshiParsingTest {

    data class FeatureCollection(
        val type: String,
        val metadata: Metadata,
        val features: List<Feature>
    )
    data class Metadata(
        val generated: Long,
        val title: String,
        val status: Int,
        val count: Int
    )
    data class Feature(
        val type: String,
        val properties: Properties,
        val geometry: Geometry,
        val id: String
    )
    data class Properties(
        val mag: Double?,
        val place: String?,
        val time: Long,
        val updated: Long,
        val url: String?,
        val detail: String?,
        val status: String?,
        val tsunami: Int?,
        val sig: Int?,
        val net: String?,
        val code: String?,
        val ids: String?,
        val magType: String?,
        val type: String?
    )
    data class Geometry(
        val type: String,
        // USGS uses [lon, lat, depth]
        val coordinates: List<Double>
    )

    private val moshi: Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    @Test
    fun parses_real_usgs_sample() {
        val json = readFixture("fixtures/usgs_one_quake.json")
        val adapter = moshi.adapter(FeatureCollection::class.java)
        val fc = requireNotNull(adapter.fromJson(json))

        assertThat(fc.type).isEqualTo("FeatureCollection")
        assertThat(fc.metadata.status).isEqualTo(200)
        assertThat(fc.metadata.count).isEqualTo(1)
        assertThat(fc.features).hasSize(1)

        val f = fc.features.first()
        assertThat(f.id).isEqualTo("ci12345678")
        assertThat(f.properties.mag).isWithin(1e-4).of(4.2)
        assertThat(f.properties.place).contains("Calipatria")
        assertThat(f.properties.time).isEqualTo(1724696400123)

        assertThat(f.geometry.type).isEqualTo("Point")
        assertThat(f.geometry.coordinates).hasSize(3)
        assertThat(f.geometry.coordinates[0]).isWithin(1e-4).of(-115.521) // lon
        assertThat(f.geometry.coordinates[1]).isWithin(1e-4).of(33.093)    // lat
        assertThat(f.geometry.coordinates[2]).isWithin(1e-4).of(6.7)       // depth
    }

    private fun readFixture(path: String): String {
        val stream: InputStream = requireNotNull(
            javaClass.classLoader?.getResourceAsStream(path)
        ) { "Fixture not found: $path" }
        return stream.source().buffer().readUtf8()
    }
}
