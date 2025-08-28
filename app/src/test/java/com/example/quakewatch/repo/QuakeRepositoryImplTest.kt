package com.example.quakewatch.repo

import com.google.common.truth.Truth.assertThat
import com.example.quakewatch.testing.MainDispatcherRule
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.test.runTest
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okio.buffer
import okio.source
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.InputStream

import com.example.quakewatch.network.UsgsApi
import com.example.quakewatch.data.QuakeRepositoryImpl
import com.example.quakewatch.domain.model.Earthquake

class QuakeRepositoryImplTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var server: MockWebServer
    private lateinit var api: UsgsApi
    private lateinit var retrofit: Retrofit

    private val moshi: Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    @Before
    fun setUp() {
        server = MockWebServer()
        server.start()

        retrofit = Retrofit.Builder()
            .baseUrl(server.url("/")) // explicit baseUrl
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(OkHttpClient.Builder().build())
            .build()

        api = retrofit.create(UsgsApi::class.java)
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun repository_returns_domain_from_usgs_response() = runTest {
        // Arrange
        val body = readFixture("fixtures/usgs_one_quake.json")
        val response = MockResponse()
            .setResponseCode(200)
            .addHeader("Content-Type", "application/json")
            .setBody(body)
        server.enqueue(response)

        val repo = QuakeRepositoryImpl(api)

        val quakes: List<Earthquake> = repo.fetchRecent()

        // Assert
        assertThat(quakes).hasSize(1)
        val eq = quakes.first()

        // Adjust field names if your domain differs (id → quakeId, magnitude → mag, etc.)
        assertThat(eq.id).isEqualTo("ci12345678")
        assertThat(eq.mag).isWithin(1e-4).of(4.2)
        assertThat(eq.title).contains("Calipatria")
        assertThat(eq.timeMillis).isEqualTo(1724696400123)

        // Only keep these if your domain carries coordinates:
        assertThat(eq.lon).isWithin(1e-4).of(-115.521)
        assertThat(eq.lat).isWithin(1e-4).of(33.093)
        assertThat(eq.depthKm).isWithin(1e-4).of(6.7)
    }

    // --- helpers ---

    private fun readFixture(path: String): String {
        val stream: InputStream = requireNotNull(
            javaClass.classLoader?.getResourceAsStream(path)
        ) { "Fixture not found: $path" }
        return stream.source().buffer().readUtf8()
    }
}
