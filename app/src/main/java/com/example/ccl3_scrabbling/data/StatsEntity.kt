package com.example.ccl3_scrabbling.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stats_table")
data class StatsEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val highestScore: Int,
    val totalWordsCompleted: Int,
    val highestScoringWord: String,
    val highestWordScore: Int
)