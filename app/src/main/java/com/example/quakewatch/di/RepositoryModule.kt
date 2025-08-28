// app/src/main/java/com/example/quakewatch/di/RepositoryModule.kt
package com.example.quakewatch.di

import com.example.quakewatch.data.QuakeRepository
import com.example.quakewatch.data.QuakeRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindQuakeRepository(impl: QuakeRepositoryImpl): QuakeRepository
}
