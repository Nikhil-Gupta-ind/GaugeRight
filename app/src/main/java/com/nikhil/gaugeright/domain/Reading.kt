package com.nikhil.gaugeright.domain

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import java.sql.Timestamp

@Entity
data class Reading(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val value: Int,
    val timestamp: Timestamp,
    val isSynced: Boolean
)

class TimestampConverter {
    @TypeConverter
    fun fromTimestamp(value: Long?): Timestamp? {
        return value?.let { Timestamp(it) }
    }

    @TypeConverter
    fun toTimestamp(timestamp: Timestamp?): Long? {
        return timestamp?.time
    }
}