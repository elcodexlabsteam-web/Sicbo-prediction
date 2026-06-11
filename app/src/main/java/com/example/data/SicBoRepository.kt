package com.example.data

import kotlinx.coroutines.flow.Flow

class SicBoRepository(private val sicBoDao: SicBoDao) {
    val allPredictions: Flow<List<SicBoEntity>> = sicBoDao.getAllPredictions()

    suspend fun insert(prediction: SicBoEntity) {
        sicBoDao.insertPrediction(prediction)
    }

    suspend fun clearAll() {
        sicBoDao.clearHistory()
    }
}
