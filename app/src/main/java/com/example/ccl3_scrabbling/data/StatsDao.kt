package com.example.ccl3_scrabbling.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface StatsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(stats: StatsEntity)

    @Update
    suspend fun update(stats: StatsEntity)

    @Query("SELECT MAX(highestWordScore) FROM stats_table")
    suspend fun getHighestWordScore(): Int?

    @Query("SELECT * FROM stats_table WHERE id = :id")
    suspend fun getStatsById(id: Int): StatsEntity?

    @Query("SELECT MAX(highestScore) FROM stats_table")
    suspend fun getHighestScore(): Int?

    @Query("SELECT totalWordsCompleted FROM stats_table WHERE id = 1")
    suspend fun getTotalWordsCompleted(): Int?

    @Query("SELECT highestScoringWord FROM stats_table ORDER BY highestScore DESC LIMIT 1")
    suspend fun getHighestScoringWord(): String?

    @Query("DELETE FROM stats_table")
    suspend fun deleteAllStats()
}