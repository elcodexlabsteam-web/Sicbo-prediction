package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SicBoDao {
    @Query("SELECT * FROM sicbo_predictions ORDER BY timestamp DESC")
    fun getAllPredictions(): Flow<List<SicBoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrediction(prediction: SicBoEntity)

    @Query("DELETE FROM sicbo_predictions")
    suspend fun clearHistory()
}
