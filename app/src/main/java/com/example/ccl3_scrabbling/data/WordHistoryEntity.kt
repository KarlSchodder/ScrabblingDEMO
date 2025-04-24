package com.example.ccl3_scrabbling.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "word_history_table")
data class WordHistoryEntity(
    @PrimaryKey val word: String,
    val timesUsed: Int,
    val totalScore: Int
)