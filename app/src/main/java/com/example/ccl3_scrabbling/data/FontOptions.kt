package com.example.ccl3_scrabbling.data

data class FontOption(
    val name: String,
    val fontFamily: androidx.compose.ui.text.font.FontFamily,
    var isUnlocked: Boolean = false
)