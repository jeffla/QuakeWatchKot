package com.example.quakewatch.di

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.example.quakewatch.network.UsgsApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton
import com.example.quakewatch.BuildConfig
import java.util.concurrent.TimeUnit

private const val BASE_URL = "https://earthquake.usgs.gov/"

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides @Singleton
    fun provideOkHttp(): OkHttpClient {
        val logger = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }

        val userAgent = "QuakeWatch/${BuildConfig.VERSION_NAME} " +
                "(contact: ${BuildConfig.CONTACT_EMAIL}; android: ${android.os.Build.VERSION.RELEASE}; " +
                "device: ${android.os.Build.MANUFACTURER} ${android.os.Build.MODEL})"

        return OkHttpClient.Builder()
            // Add UA header to every request (overwrites any existing UA)
            .addInterceptor { chain ->
                val req = chain.request().newBuilder()
                    .header("User-Agent", userAgent)
                    .build()
                chain.proceed(req)
            }
            .addInterceptor(logger)
            .connectTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
            .writeTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
            .build()
    }

    @Provides @Singleton
    fun provideMoshi(): Moshi =
        Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory()) // <-- important
            .build()

    @Provides @Singleton
    fun provideRetrofit(client: OkHttpClient, moshi: Moshi): Retrofit =
        Retrofit.Builder()
            .baseUrl("https://earthquake.usgs.gov/")
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi)) // <-- use that Moshi
            .build()

    @Provides @Singleton
    fun provideUsgsApi(retrofit: Retrofit): UsgsApi =
        retrofit.create(UsgsApi::class.java)
}