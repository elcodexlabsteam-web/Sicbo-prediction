package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.SicBoEntity
import com.example.data.SicBoRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SicBoStats(
    val totalSpins: Int = 0,
    val leopardCount: Int = 0,
    val kecilCount: Int = 0,
    val besarCount: Int = 0,
    val ganjilCount: Int = 0,
    val genapCount: Int = 0,
    val leopardPercentage: Float = 0f,
    val kecilPercentage: Float = 0f,
    val besarPercentage: Float = 0f,
    val ganjilPercentage: Float = 0f,
    val genapPercentage: Float = 0f
)

class SicBoViewModel(private val repository: SicBoRepository) : ViewModel() {

    // Inputs
    private val _inputSeed = MutableStateFlow("")
    val inputSeed: StateFlow<String> = _inputSeed.asStateFlow()

    // Loading State
    private val _isRolling = MutableStateFlow(false)
    val isRolling: StateFlow<Boolean> = _isRolling.asStateFlow()

    // Current Rolled/Prediction outcome
    private val _currentResult = MutableStateFlow<SicBoEntity?>(null)
    val currentResult: StateFlow<SicBoEntity?> = _currentResult.asStateFlow()

    // Historical predictions from DB
    val history: StateFlow<List<SicBoEntity>> = repository.allPredictions
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun onSeedChange(newVal: String) {
        // Enforce max lengths or digit-only validations
        if (newVal.length <= 3 && newVal.all { it.isDigit() }) {
            _inputSeed.value = newVal
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            repository.clearAll()
            _currentResult.value = null
        }
    }

    fun triggerPrediction() {
        val seed = _inputSeed.value
        if (seed.length != 3) return

        viewModelScope.launch {
            _isRolling.value = true
            _currentResult.value = null

            // Delay simulating high-end cryptographic "SPINNING DICE" analysis
            delay(2000)

            val previousResults = history.value
            val rolls = generateRolls(seed, previousResults)
            
            val die1 = rolls.first
            val die2 = rolls.second
            val die3 = rolls.third
            val total = die1 + die2 + die3
            
            val isLeopard = (die1 == die2 && die2 == die3)
            val isKecil = (total in 4..10) && !isLeopard
            val isBesar = (total in 11..17) && !isLeopard
            val isGanjil = (total % 2 != 0)
            val isGenap = (total % 2 == 0)

            val prediction = SicBoEntity(
                seed = seed,
                die1 = die1,
                die2 = die2,
                die3 = die3,
                total = total,
                isLeopard = isLeopard,
                isKecil = isKecil,
                isBesar = isBesar,
                isGanjil = isGanjil,
                isGenap = isGenap
            )

            // Insert into local persistent Room database
            repository.insert(prediction)
            
            // Set the result
            _currentResult.value = prediction
            _isRolling.value = false
        }
    }

    private fun generateRolls(seed: String, previousResults: List<SicBoEntity>): Triple<Int, Int, Int> {
        val numericInput = seed.toIntOrNull() ?: 123
        var attempts = 0
        var r1 = 1
        var r2 = 3
        var r3 = 5

        while (attempts < 15) {
            val currentSeed = (numericInput * 311).toLong() + System.currentTimeMillis() + (attempts * 17)
            val random = java.util.Random(currentSeed)
            r1 = random.nextInt(6) + 1
            r2 = random.nextInt(6) + 1
            r3 = random.nextInt(6) + 1

            val total = r1 + r2 + r3
            val isLeopard = (r1 == r2 && r2 == r3)
            val isKecil = (total in 4..10) && !isLeopard
            val isBesar = (total in 11..17) && !isLeopard

            // Anti-pola check: prevent continuous alternating or static repetition
            if (previousResults.size >= 3) {
                val last1 = previousResults[0]
                val last2 = previousResults[1]
                val last3 = previousResults[2]

                val is1Kecil = last1.isKecil
                val is2Kecil = last2.isKecil
                val is3Kecil = last3.isKecil

                val is1Besar = last1.isBesar
                val is2Besar = last2.isBesar
                val is3Besar = last3.isBesar

                val wouldAlternating = (isKecil && is1Besar && is2Kecil && is3Besar) ||
                                       (isBesar && is1Kecil && is2Besar && is3Kecil)

                val wouldBeFourInARow = (isKecil && is1Kecil && is2Kecil && is3Kecil) ||
                                        (isBesar && is1Besar && is2Besar && is3Besar)

                if (wouldAlternating || wouldBeFourInARow) {
                    attempts++
                    continue
                }
            }
            break
        }
        return Triple(r1, r2, r3)
    }

    // Helper to calculate statistics
    fun getStats(predictionsList: List<SicBoEntity>): SicBoStats {
        val total = predictionsList.size
        if (total == 0) return SicBoStats()

        val leopards = predictionsList.count { it.isLeopard }
        val kecil = predictionsList.count { it.isKecil }
        val besar = predictionsList.count { it.isBesar }
        val ganjil = predictionsList.count { it.isGanjil }
        val genap = predictionsList.count { it.isGenap }

        return SicBoStats(
            totalSpins = total,
            leopardCount = leopards,
            kecilCount = kecil,
            besarCount = besar,
            ganjilCount = ganjil,
            genapCount = genap,
            leopardPercentage = (leopards.toFloat() / total) * 100,
            kecilPercentage = (kecil.toFloat() / total) * 100,
            besarPercentage = (besar.toFloat() / total) * 100,
            ganjilPercentage = (ganjil.toFloat() / total) * 100,
            genapPercentage = (genap.toFloat() / total) * 100
        )
    }
}

class SicBoViewModelFactory(private val repository: SicBoRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SicBoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SicBoViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
