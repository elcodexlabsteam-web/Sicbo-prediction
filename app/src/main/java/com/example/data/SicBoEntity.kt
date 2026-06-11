package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sicbo_predictions")
data class SicBoEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val seed: String,
    val die1: Int,
    val die2: Int,
    val die3: Int,
    val total: Int,
    val isLeopard: Boolean,
    val isKecil: Boolean,
    val isBesar: Boolean,
    val isGanjil: Boolean,
    val isGenap: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)
