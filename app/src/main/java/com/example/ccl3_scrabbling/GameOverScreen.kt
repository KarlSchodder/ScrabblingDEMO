package com.example.ccl3_scrabbling

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.ui.platform.LocalContext
import com.example.ccl3_scrabbling.data.FontRepository
import com.example.ccl3_scrabbling.ui.theme.BrownLight

// Helper function moved outside the Composable
private fun getMultiplierColor(multiplier: Int): Color = when (multiplier) {
    2 -> Color(0xFF1E88E5) // Material Blue
    3 -> Color(0xFF43A047) // Material Green
    4 -> Color(0xFF8E24AA) // Material Purple
    else -> Color.Unspecified
}

@Composable
fun GameOverScreen(
    navController: NavHostController,
    score: Int,
    wordsCompleted: Int,
    completedWordsString: String,
    statsViewModel: StatsViewModel
) {
    data class WordEntry(
        val word: String,
        val score: Int,
        val multiplier: Int
    )
    val soundManager = rememberSoundManager()

    // Parse completed words string
    val completedWords = completedWordsString
        .takeIf { it.isNotEmpty() }
        ?.split(",")
        ?.map { entry ->
            val (word, score, multiplier) = entry.split(":")
            WordEntry(word, score.toInt(), multiplier.toInt())
        } ?: emptyList()

    // Find highest scoring word
    val highestScoringWord = completedWords.maxByOrNull { it.score }

    var newFontUnlocked by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        soundManager.playGameOver()

        statsViewModel.update(
            score = score,
            wordsCompleted = wordsCompleted,
            highestScoringWord = highestScoringWord?.word ?: "",
            highestWordScore = highestScoringWord?.score ?: 0,
            completedWordsString = completedWordsString
        )

        // Check if any font name is in the completedWords and unlock it
        completedWords.forEach { wordEntry ->
            if (FontRepository.unlockFont(wordEntry.word)) {
                newFontUnlocked = wordEntry.word
            }
        }
    }

    // Function to create dictionary URL
    fun getDictionaryUrl(word: String): String {
        return "https://www.merriam-webster.com/dictionary/${word.lowercase()}"
    }

    val context = LocalContext.current

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .height(72.dp)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "GAME OVER",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "$score",
                style = MaterialTheme.typography.headlineLarge,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Words Completed: $wordsCompleted",
                style = MaterialTheme.typography.bodyLarge
            )

            // Highest scoring word section
            if (highestScoringWord != null) {
                Spacer(modifier = Modifier.height(24.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clickable {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(getDictionaryUrl(highestScoringWord.word)))
                            context.startActivity(intent)
                        },
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Highest Scoring Word",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = highestScoringWord.word,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (highestScoringWord.multiplier > 1) {
                                    Icon(
                                        imageVector = Icons.Filled.Star,
                                        contentDescription = "Multiplier ${highestScoringWord.multiplier}x",
                                        tint = getMultiplierColor(highestScoringWord.multiplier),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Text(
                                    text = "${highestScoringWord.score}",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = Color.Black,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // Word history section
            if (completedWords.isNotEmpty()) {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Word History",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(completedWords.size) { index ->
                            val wordEntry = completedWords[index]
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp)
                                    .clickable {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(getDictionaryUrl(wordEntry.word)))
                                        context.startActivity(intent)
                                    },
                                colors = CardDefaults.cardColors(
                                    containerColor = BrownLight
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = wordEntry.word,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        if (wordEntry.multiplier > 1) {
                                            Icon(
                                                imageVector = Icons.Filled.Star,
                                                contentDescription = "Multiplier ${wordEntry.multiplier}x",
                                                tint = getMultiplierColor(wordEntry.multiplier),
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                        Text(
                                            // Always show corrected score in word history
                                            text = "${wordEntry.score / wordEntry.multiplier}",
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = Color.Black
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

// Show special message if a new font is unlocked
            newFontUnlocked?.let {
                Spacer(modifier = Modifier.height(24.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Congratulations!",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "You've unlocked the $it font!",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            // Navigation buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(onClick = {
                    navController.navigate("gameScreen") {
                        popUpTo("gameScreen") { inclusive = true }
                    }
                }) {
                    Text(text = "Play Again")
                }
                Button(onClick = {
                    navController.navigate("startScreen") {
                        popUpTo("startScreen") { inclusive = true }
                    }
                }) {
                    Text(text = "Start Menu")
                }
            }
        }
    }
}