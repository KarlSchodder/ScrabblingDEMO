package com.example.ccl3_scrabbling.data

import androidx.compose.runtime.mutableStateOf
import androidx.room.Room
import com.example.ccl3_scrabbling.ui.theme.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object FontRepository {
    private val fonts = listOf(
        FontOption("Roboto", Roboto, true),
        FontOption("Calibri", Calibri, true),
        FontOption("Bold", Bold, true),
        FontOption("Times New Roman", Times, true),
        FontOption("Newspaper", Newspaper, true),
        FontOption("Dutch", Dutch, false),
        FontOption("Bauhaus", Design, false),
        FontOption("COLLEGE", College, false),
        FontOption("Barney", Barney, false),
        FontOption("Bubbly", Bubbly, false),
        FontOption("Kratos", Epic, false),
        FontOption("Rune", Rune, false),
        FontOption("Gothic", Gothic, false),
        FontOption("Pixel", Blocky, false),
        FontOption("Psychedelic", Psychedelic, false),
        FontOption("Ninja", Ninja, false),
        FontOption("Vehicular", Vehicular, false),
        FontOption("Wizard", Harry, false),
        FontOption("Squid", Squid, false),
        FontOption("Tropical", Tropics, false),
        FontOption("NASA", NASA, false),
        FontOption("Lobster", Lobster, false),
        FontOption("Graffiti", Graffiti, false),
        FontOption("Alhambra", Alhambra, false)
    )

    var currentFontIndex = mutableStateOf(0)
        private set

    private lateinit var settingsDao: SettingsDao

    fun initializeDatabase(database: OfflineDatabase) {
        settingsDao = database.settingsDao()
        loadSettings()
    }

    private fun loadSettings() {
        CoroutineScope(Dispatchers.IO).launch {
            val settings = settingsDao.getSettings()
            settings?.let {
                it.unlockedFonts.split(",").forEach { fontName ->
                    val fontIndex = fonts.indexOfFirst { it.name == fontName }
                    if (fontIndex != -1) {
                        fonts[fontIndex].isUnlocked = true
                    }
                }
                val currentFontIndex = fonts.indexOfFirst { font -> font.name == settings.currentFont }
                if (currentFontIndex != -1) {
                    this@FontRepository.currentFontIndex.value = currentFontIndex
                }
            }
        }
    }

    private fun saveSettings() {
        CoroutineScope(Dispatchers.IO).launch {
            val unlockedFonts = fonts.filter { it.isUnlocked }.joinToString(",") { it.name }
            val currentFont = getCurrentFont().name
            val settings = SettingsEntity(id = 1, unlockedFonts = unlockedFonts, currentFont = currentFont)
            settingsDao.insert(settings)
        }
    }

    fun getUnlockedFonts(): List<FontOption> =
        fonts.filter { it.isUnlocked }

    fun getCurrentFont(): FontOption =
        fonts[currentFontIndex.value]

    fun unlockFont(fontName: String): Boolean {
        val fontIndex = fonts.indexOfFirst { it.name == fontName }
        return if (fontIndex != -1 && !fonts[fontIndex].isUnlocked) {
            fonts[fontIndex].isUnlocked = true
            saveSettings()
            true
        } else {
            false
        }
    }

    fun setNextFont() {
        val unlockedFonts = getUnlockedFonts()
        val currentIndexInUnlocked = unlockedFonts.indexOfFirst {
            it.name == getCurrentFont().name
        }
        currentFontIndex.value = if (currentIndexInUnlocked < unlockedFonts.size - 1) {
            fonts.indexOfFirst { it.name == unlockedFonts[currentIndexInUnlocked + 1].name }
        } else {
            fonts.indexOfFirst { it.name == unlockedFonts[0].name }
        }
        saveSettings()
    }

    fun setPreviousFont() {
        val unlockedFonts = getUnlockedFonts()
        val currentIndexInUnlocked = unlockedFonts.indexOfFirst {
            it.name == getCurrentFont().name
        }
        currentFontIndex.value = if (currentIndexInUnlocked > 0) {
            fonts.indexOfFirst { it.name == unlockedFonts[currentIndexInUnlocked - 1].name }
        } else {
            fonts.indexOfFirst { it.name == unlockedFonts.last().name }
        }
        saveSettings()
    }

    fun setCurrentFont(font: FontOption) {
        val fontIndex = fonts.indexOfFirst { it.name == font.name }
        if (fontIndex != -1) {
            currentFontIndex.value = fontIndex
            saveSettings()
        }
    }
}