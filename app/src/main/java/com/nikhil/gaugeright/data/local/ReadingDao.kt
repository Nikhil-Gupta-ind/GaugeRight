package com.nikhil.gaugeright.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.nikhil.gaugeright.domain.Reading
import kotlinx.coroutines.flow.Flow

@Dao
interface ReadingDao {

    @Upsert
    suspend fun upsertReadings(reading: List<Reading>)

    @Query("SELECT * FROM reading")
    fun getAllReadings(): Flow<List<Reading>>

    @Query("SELECT * FROM reading ORDER BY timestamp DESC")
    fun getLatestReadings(): Flow<List<Reading>>

    @Query("SELECT * FROM reading WHERE isSynced = 0")
    suspend fun getUnsyncedData(): List<Reading>

    @Query("UPDATE reading SET isSynced = 1 WHERE id IN (:ids)")
    suspend fun updateSyncStatus(ids: List<Int>)
}