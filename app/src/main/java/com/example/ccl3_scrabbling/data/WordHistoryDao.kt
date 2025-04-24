package com.example.ccl3_scrabbling.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface WordHistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(wordHistory: WordHistoryEntity)

    @Query("SELECT * FROM word_history_table WHERE word = :word")
    suspend fun getWordHistory(word: String): WordHistoryEntity?

    @Query("SELECT * FROM word_history_table ORDER BY timesUsed DESC LIMIT 1")
    suspend fun getMostUsedWord(): WordHistoryEntity?

    @Query("SELECT * FROM word_history_table ORDER BY timesUsed DESC")
    suspend fun getAllWordHistory(): List<WordHistoryEntity>

    @Query("DELETE FROM word_history_table")
    suspend fun deleteAllWordHistory()
}