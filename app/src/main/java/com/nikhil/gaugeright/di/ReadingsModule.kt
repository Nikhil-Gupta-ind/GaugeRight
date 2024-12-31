package com.nikhil.gaugeright.di

import android.content.Context
import com.nikhil.gaugeright.data.ai.SimulatedGaugeReader
import com.nikhil.gaugeright.data.local.ReadingDao
import com.nikhil.gaugeright.data.local.ReadingDatabase
import com.nikhil.gaugeright.data.remote.ApiService
import com.nikhil.gaugeright.domain.GaugeReader
import com.nikhil.gaugeright.domain.ReadingRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ReadingsModule {

    @Singleton
    @Provides
    fun providesGaugeReader(gaugeReader: SimulatedGaugeReader): GaugeReader {
        return gaugeReader
    }

    @Singleton
    @Provides
    fun providesReadingRepository(readingLocalRepository: com.nikhil.gaugeright.data.ReadingRepositoryImpl): ReadingRepository {
        return readingLocalRepository
    }

    @Singleton
    @Provides
    fun providesReadingDao(@ApplicationContext context: Context): ReadingDao {
        return ReadingDatabase.getDatabase(context).readingDao()
    }

    @Singleton
    @Provides
    fun providesApiService(): ApiService {
        return Retrofit.Builder()
            .baseUrl("https://jsonplaceholder.typicode.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(ApiService::class.java)
    }
}