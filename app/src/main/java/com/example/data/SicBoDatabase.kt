package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [SicBoEntity::class], version = 1, exportSchema = false)
abstract class SicBoDatabase : RoomDatabase() {
    abstract fun sicBoDao(): SicBoDao

    companion object {
        @Volatile
        private var INSTANCE: SicBoDatabase? = null

        fun getDatabase(context: Context): SicBoDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SicBoDatabase::class.java,
                    "sicbo_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
