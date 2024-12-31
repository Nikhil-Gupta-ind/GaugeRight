package com.nikhil.gaugeright.domain

import androidx.work.WorkInfo
import com.nikhil.gaugeright.data.remote.Resource
import kotlinx.coroutines.flow.Flow

interface ReadingRepository {

    val syncInfo: Flow<WorkInfo?>?

    fun getReadings(): Flow<List<Reading>>

    fun getLatestReadings(): Flow<List<Reading>>

    suspend fun insertReading(reading: Reading)

    suspend fun upload(readings: List<Reading>): Flow<Resource<Any>>

    fun syncNow(readings: List<Reading>)

}