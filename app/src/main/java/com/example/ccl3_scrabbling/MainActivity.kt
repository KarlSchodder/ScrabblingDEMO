package com.example.ccl3_scrabbling

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Typography
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ccl3_scrabbling.data.DatabaseProvider
import com.example.ccl3_scrabbling.data.loadDictionaryContent
import com.example.ccl3_scrabbling.data.FontRepository
import com.example.ccl3_scrabbling.data.StatsDao
import com.example.ccl3_scrabbling.ui.theme.Ccl3scrabblingTheme
import com.example.ccl3_scrabbling.data.WordHistoryDao

class MainActivity : ComponentActivity() {
    private val statsDao by lazy { DatabaseProvider.getDatabase(this).statsDao() }
    private val wordHistoryDao by lazy { DatabaseProvider.getDatabase(this).wordHistoryDao() }
    private val settingsDao by lazy { DatabaseProvider.getDatabase(this).settingsDao() }

    private val statsViewModel: StatsViewModel by viewModels {
        StatsViewModelFactory(statsDao, wordHistoryDao)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadDictionaryContent(this)
        Log.d("Dictionary", "main activity - dictionary loaded")
        enableEdgeToEdge()

        // Initialize FontRepository with the database
        val database = DatabaseProvider.getDatabase(this)
        FontRepository.initializeDatabase(database)

        setContent {
            val navController = rememberNavController()

            // Observe the current font selection
            val currentFont by remember { FontRepository.currentFontIndex }

            // Create custom typography with the selected font
            val updatedTypography = Typography(
                displayLarge = MaterialTheme.typography.displayLarge.copy(
                    fontFamily = FontRepository.getCurrentFont().fontFamily
                ),
                displayMedium = MaterialTheme.typography.displayMedium.copy(
                    fontFamily = FontRepository.getCurrentFont().fontFamily
                ),
                displaySmall = MaterialTheme.typography.displaySmall.copy(
                    fontFamily = FontRepository.getCurrentFont().fontFamily
                ),
                headlineLarge = MaterialTheme.typography.headlineLarge.copy(
                    fontFamily = FontRepository.getCurrentFont().fontFamily
                ),
                headlineMedium = MaterialTheme.typography.headlineMedium.copy(
                    fontFamily = FontRepository.getCurrentFont().fontFamily
                ),
                headlineSmall = MaterialTheme.typography.headlineSmall.copy(
                    fontFamily = FontRepository.getCurrentFont().fontFamily
                ),
                titleLarge = MaterialTheme.typography.titleLarge.copy(
                    fontFamily = FontRepository.getCurrentFont().fontFamily
                ),
                titleMedium = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = FontRepository.getCurrentFont().fontFamily
                ),
                titleSmall = MaterialTheme.typography.titleSmall.copy(
                    fontFamily = FontRepository.getCurrentFont().fontFamily
                ),
                bodyLarge = MaterialTheme.typography.bodyLarge.copy(
                    fontFamily = FontRepository.getCurrentFont().fontFamily
                ),
                bodyMedium = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = FontRepository.getCurrentFont().fontFamily
                ),
                bodySmall = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = FontRepository.getCurrentFont().fontFamily
                ),
                labelLarge = MaterialTheme.typography.labelLarge.copy(
                    fontFamily = FontRepository.getCurrentFont().fontFamily
                ),
                labelMedium = MaterialTheme.typography.labelMedium.copy(
                    fontFamily = FontRepository.getCurrentFont().fontFamily
                ),
                labelSmall = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontRepository.getCurrentFont().fontFamily
                )
            )

            Ccl3scrabblingTheme(
                typography = updatedTypography // Pass the updated typography to your theme
            ) {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "startScreen",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("startScreen") {
                            StartScreen(navController)
                        }
                        composable("gameScreen") {
                            GameScreen(navController)
                        }
                        composable("statsScreen") {
                            StatsScreen(navController, statsViewModel)
                        }
                        composable("settingsScreen") {
                            SettingsScreen(
                                navController = navController,
                                statsViewModel = statsViewModel
                            )
                        }
                        composable("gameOverScreen/{score}/{wordsCompleted}/{completedWords}") { backStackEntry ->
                            val score = backStackEntry.arguments?.getString("score")?.toInt() ?: 0
                            val wordsCompleted = backStackEntry.arguments?.getString("wordsCompleted")?.toInt() ?: 0
                            val completedWords = backStackEntry.arguments?.getString("completedWords") ?: ""
                            GameOverScreen(navController, score, wordsCompleted, completedWords, statsViewModel)
                        }
                    }
                }
            }
        }
    }
}

class StatsViewModelFactory(
    private val statsDao: StatsDao,
    private val wordHistoryDao: WordHistoryDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StatsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StatsViewModel(statsDao, wordHistoryDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}