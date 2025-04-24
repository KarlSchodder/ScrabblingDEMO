package com.example.ccl3_scrabbling.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "settings_table")
data class SettingsEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val unlockedFonts: String, // Stores unlocked fonts
    val currentFont: String // Stores the current font
)