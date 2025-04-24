package com.example.ccl3_scrabbling

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ccl3_scrabbling.data.StatsDao
import com.example.ccl3_scrabbling.data.StatsEntity
import com.example.ccl3_scrabbling.data.WordHistoryDao
import com.example.ccl3_scrabbling.data.WordHistoryEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class StatsViewModel(
    private val statsDao: StatsDao,
    private val wordHistoryDao: WordHistoryDao
) : ViewModel() {
    private val _highestScore = MutableStateFlow(0)
    val highestScore: StateFlow<Int> = _highestScore

    private val _totalWordsCompleted = MutableStateFlow(0)
    val totalWordsCompleted: StateFlow<Int> = _totalWordsCompleted

    private val _highestScoringWord = MutableStateFlow("")
    val highestScoringWord: StateFlow<String> = _highestScoringWord

    private val _highestWordScore = MutableStateFlow(0)
    val highestWordScore: StateFlow<Int> = _highestWordScore

    private val _mostUsedWord = MutableStateFlow<WordHistoryEntity?>(null)
    val mostUsedWord: StateFlow<WordHistoryEntity?> = _mostUsedWord

    init {
        viewModelScope.launch {
            // Get existing stats or create initial stats
            val currentStats = statsDao.getStatsById(1) ?: StatsEntity(
                id = 1,
                highestScore = 0,
                totalWordsCompleted = 0,
                highestScoringWord = "",
                highestWordScore = 0
            ).also { statsDao.insert(it) }

            // Initialize StateFlows with current values
            _highestScore.value = currentStats.highestScore
            _totalWordsCompleted.value = currentStats.totalWordsCompleted
            _highestScoringWord.value = currentStats.highestScoringWord
            _highestWordScore.value = currentStats.highestWordScore
            _mostUsedWord.value = wordHistoryDao.getMostUsedWord()
        }
    }

    private suspend fun updateWordHistory(word: String, score: Int) {
        val existingWord = wordHistoryDao.getWordHistory(word)
        val newWordHistory = if (existingWord != null) {
            existingWord.copy(
                timesUsed = existingWord.timesUsed + 1,
                totalScore = existingWord.totalScore + score
            )
        } else {
            WordHistoryEntity(
                word = word,
                timesUsed = 1,
                totalScore = score
            )
        }
        wordHistoryDao.insert(newWordHistory)
        _mostUsedWord.value = wordHistoryDao.getMostUsedWord()
    }

    fun update(
        score: Int,
        wordsCompleted: Int,
        highestScoringWord: String,
        highestWordScore: Int,
        completedWordsString: String = "" // Add this parameter
    ) {
        viewModelScope.launch {
            // Process all completed words if provided
            if (completedWordsString.isNotEmpty()) {
                completedWordsString.split(",").forEach { entry ->
                    val (word, score, multiplier) = entry.split(":")
                    updateWordHistory(word, score.toInt())
                }
            }

            val currentStats = statsDao.getStatsById(1) ?: StatsEntity(
                id = 1,
                highestScore = 0,
                totalWordsCompleted = 0,
                highestScoringWord = "",
                highestWordScore = 0
            )

            // Check if we have a new highest scoring word
            val (finalWord, finalWordScore) = if (highestWordScore > currentStats.highestWordScore) {
                highestScoringWord to highestWordScore
            } else {
                currentStats.highestScoringWord to currentStats.highestWordScore
            }

            // Create new stats with updated values
            val newStats = StatsEntity(
                id = 1,  // Always use ID 1
                highestScore = maxOf(score, currentStats.highestScore),
                totalWordsCompleted = currentStats.totalWordsCompleted + wordsCompleted,
                highestScoringWord = finalWord,
                highestWordScore = finalWordScore
            )

            statsDao.insert(newStats)

            // Update StateFlows
            _highestScore.value = newStats.highestScore
            _totalWordsCompleted.value = newStats.totalWordsCompleted
            _highestScoringWord.value = newStats.highestScoringWord
            _highestWordScore.value = newStats.highestWordScore
        }
    }
    fun clearAllStats() {
        viewModelScope.launch {
            // Clear all data from both tables
            statsDao.deleteAllStats()
            wordHistoryDao.deleteAllWordHistory()

            // Reset all StateFlows to initial values
            _highestScore.value = 0
            _totalWordsCompleted.value = 0
            _highestScoringWord.value = ""
            _highestWordScore.value = 0
            _mostUsedWord.value = null

            // Reinitialize stats with default values
            statsDao.insert(StatsEntity(
                id = 1,
                highestScore = 0,
                totalWordsCompleted = 0,
                highestScoringWord = "",
                highestWordScore = 0
            ))
        }
    }
}