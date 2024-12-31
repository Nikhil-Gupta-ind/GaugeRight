package com.nikhil.gaugeright.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.nikhil.gaugeright.domain.Reading
import com.nikhil.gaugeright.domain.TimestampConverter

@Database(entities = [Reading::class], version = 1)
@TypeConverters(TimestampConverter::class)
abstract class ReadingDatabase: RoomDatabase() {

    abstract fun readingDao(): ReadingDao

    companion object {

        @Volatile
        private var INSTANCE: ReadingDatabase? = null

        fun getDatabase(context: Context): ReadingDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = androidx.room.Room.databaseBuilder(
                    context.applicationContext,
                    ReadingDatabase::class.java,
                    "readings_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}