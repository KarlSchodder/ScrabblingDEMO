package com.example.ccl3_scrabbling.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface SettingsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(settings: SettingsEntity)

    @Update
    suspend fun update(settings: SettingsEntity)

    @Query("SELECT * FROM settings_table WHERE id = 1")
    suspend fun getSettings(): SettingsEntity?

    @Query("UPDATE settings_table SET currentFont = :font WHERE id = 1")
    suspend fun updateCurrentFont(font: String)
}