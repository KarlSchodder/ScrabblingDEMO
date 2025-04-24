package com.example.ccl3_scrabbling


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.text.font.FontWeight
import com.example.ccl3_scrabbling.data.dictionary
import com.example.ccl3_scrabbling.ui.theme.BrownDark
import kotlinx.coroutines.delay
import androidx.compose.animation.core.*
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.foundation.layout.offset
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.unit.fontscaling.MathUtils.lerp
import com.example.ccl3_scrabbling.ui.theme.BrownLight
import com.example.ccl3_scrabbling.ui.theme.RedLight


data class CompletedWord(
    val word: String,
    val points: Int,
    val multiplier: Int
)


fun canFormValidWord(currentWord: String, newLetter: Char, dictionary: List<String>, requiredLength: Int): Boolean {
    val wordWithNewLetter = currentWord + newLetter
    // If adding this letter would exceed the required length, return false
    if (wordWithNewLetter.length > requiredLength) return false


    // For the final letter (when word would be complete)
    if (wordWithNewLetter.length == requiredLength) {
        // Check if this forms a complete valid word
        return wordWithNewLetter.toLowerCase() in dictionary
    }


    // For intermediate letters, check if any word of EXACTLY the required length exists
    return dictionary.any { word ->
        word.length == requiredLength &&
                word.startsWith(wordWithNewLetter.toLowerCase())
    }
}


fun getNextWordLength(): Int {
    val random = Math.random()
    return when {
        random < 0.20 -> 4  // 20%
        random < 0.45 -> 5  // 25%
        random < 0.65 -> 6  // 20%
        random < 0.80 -> 7  // 15%
        random < 0.90 -> 8  // 10%
        random < 0.95 -> 9  // 5%
        random < 0.98 -> 10 // 3%
        random < 0.99 -> 11 // 1%
        else -> 12          // 1%
    }
}


object ScrabblePoints {
    private val letterPoints = mapOf(
        'A' to 1, 'B' to 3, 'C' to 3, 'D' to 2, 'E' to 1,
        'F' to 4, 'G' to 2, 'H' to 4, 'I' to 1, 'J' to 8,
        'K' to 5, 'L' to 1, 'M' to 3, 'N' to 1, 'O' to 1,
        'P' to 3, 'Q' to 10, 'R' to 1, 'S' to 1, 'T' to 1,
        'U' to 1, 'V' to 4, 'W' to 4, 'X' to 8, 'Y' to 4,
        'Z' to 10
    )


    fun getLetterPoints(letter: Char): Int {
        return letterPoints[letter.uppercaseChar()] ?: 0
    }


    fun calculateWordScore(word: String, wordLength: Int, completedWords: Int): Int {
        // Calculate base points from letters
        val basePoints = word.sumOf { getLetterPoints(it) }


        // Apply multiplier: (2 + (wordLength/10) + (completedWords/10))
        val multiplier = 2.0 + (wordLength / 10.0) + (completedWords / 10.0)


        // Multiply and round up to nearest integer
        return kotlin.math.ceil(basePoints * multiplier).toInt()
    }
}


fun updateTimer(
    currentTime: Float,
    maxTime: Float,
    points: Int,
    onTimeUpdated: (Float) -> Unit,
    onBonusVisible: () -> Unit
) {
    val newTime = (currentTime + points).coerceAtMost(maxTime)
    onTimeUpdated(newTime)
    onBonusVisible()
}


// Game Timer
@Composable
fun GameTimer(
    maxSelectedLetters: Int,
    wordsCompleted: Int,
    modifier: Modifier = Modifier,
    onTimeUp: () -> Unit
) {
    // Calculate initial time based on requirements
    val baseTime = 30f
    val letterBonus = maxSelectedLetters * 2f
    val wordPenalty = wordsCompleted.toFloat()
    var maxTime by remember(maxSelectedLetters, wordsCompleted) {
        mutableFloatStateOf(
            (baseTime + letterBonus - wordPenalty)
                .coerceAtLeast(1f + maxSelectedLetters)
        )
    }
    var currentTime by remember { mutableFloatStateOf(maxTime) }
    var bonusTimeAnimation by remember { mutableFloatStateOf(0f) }
    var bonusTimeVisible by remember { mutableStateOf(false) }


    // Timer animation
    LaunchedEffect(currentTime, maxTime) {
        if (currentTime > 0) {
            delay(100)
            currentTime -= 0.1f
            if (currentTime <= 0) {
                onTimeUp()
            }
        }
    }


    // Bonus time animation
    LaunchedEffect(bonusTimeVisible) {
        if (bonusTimeVisible) {
            animate(
                initialValue = 0f,
                targetValue = 1f,
                animationSpec = tween(1000)
            ) { value, _ ->
                bonusTimeAnimation = value
            }
            delay(1000)
            bonusTimeVisible = false
        }
    }


    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(24.dp)
    ) {
        // Timer bar
        LinearProgressIndicator(
            progress = (currentTime / maxTime).coerceIn(0f, 1f),
            modifier = Modifier
                .fillMaxWidth()
                .height(24.dp),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.secondary
        )


        // Timer text
        Text(
            text = String.format("%.1f", currentTime),
            modifier = Modifier.align(Alignment.Center),
            color = MaterialTheme.colorScheme.onPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )


        // Bonus time indicator
        if (bonusTimeVisible) {
            Text(
                text = "+${bonusTimeAnimation}s",
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 8.dp)
                    .graphicsLayer(alpha = 1f - bonusTimeAnimation),
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}


@Composable
fun GameScreen(navController: NavHostController, hardcore: Boolean = false) {
    // SoundManager
    val soundManager = rememberSoundManager()


    // Grid size
    val gridSize = 9
    val totalCells = gridSize * gridSize
    // initialize LetterManager
    val letterManager = remember { LetterManager() }
    // Initialize lettersList with 10 random letters
    var letters by remember { mutableStateOf<List<Char?>>(
        letterManager.generateInitialLetters(totalCells, 10)) }
    var selectedLetters by remember { mutableStateOf("") }
    // Shuffle indexes to randomize order, only once
    val indexes = remember { (0 until totalCells).toList().shuffled() }


    // Track completed words and their scores
    data class WordEntry(
        val word: String,
        val score: Int,
        val multiplier: Int,
        val timestamp: Long = System.currentTimeMillis()
    )
    var completedWords by remember { mutableStateOf<List<WordEntry>>(listOf()) }
    var lastCompletedWord by remember { mutableStateOf<WordEntry?>(null) }


    // Available dictionary that removes completed words in hardcore mode
    var availableDictionary by remember { mutableStateOf(dictionary) }


    // game states
    var totalScore by remember { mutableIntStateOf(0) }
    var wordsCompleted by remember { mutableIntStateOf(0) }
    var maxSelectedLetters by remember { mutableIntStateOf(4) }
    var letterTimers by remember { mutableStateOf(mutableMapOf<Int, Long>()) }
    var scoreMultiplier by remember { mutableIntStateOf(1) }


    // Timer states
    val baseTime = 26f
    val letterBonus = maxSelectedLetters * 2f
    val wordPenalty = wordsCompleted.toFloat()
    var maxTime by remember(maxSelectedLetters, wordsCompleted) {
        mutableFloatStateOf(
            (baseTime + letterBonus - wordPenalty)
                .coerceAtLeast(10f + maxSelectedLetters)
        )
    }
    var currentTime by remember { mutableFloatStateOf(maxTime) }
    var bonusTimeVisible by remember { mutableStateOf(false) }
    var timerVisible by remember { mutableStateOf(true) }
    var lastPointsAdded by remember { mutableIntStateOf(0) }


    // Timer countdown effect
    LaunchedEffect(currentTime, maxTime) {
        if (currentTime > 0 && timerVisible) {
            delay(100)
            currentTime -= 0.1f
            if (currentTime <= 0) {
                timerVisible = false
                navController.navigate("gameOverScreen/${totalScore}/${wordsCompleted}/${completedWords.map { "${it.word}:${it.score}:${it.multiplier}" }.joinToString(",")}")
            }
        }
    }


    LaunchedEffect(currentTime) {
        if (currentTime <= 10) {
            soundManager.playTimerWarning()
        } else {
            soundManager.stopTimerWarning()
        }
    }


    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Timer at the top
            if (timerVisible) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(24.dp)
                ) {
                    // Timer bar
                    LinearProgressIndicator(
                        progress = (currentTime / maxTime).coerceIn(0f, 1f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(24.dp),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.secondary
                    )


                    // Timer text
                    Text(
                        text = String.format("%.1f", currentTime),
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )


                    // Bonus time indicator
                    if (bonusTimeVisible) {
                        var bonusTimeAnimation by remember { mutableFloatStateOf(0f) }
                        LaunchedEffect(bonusTimeVisible) {
                            animate(
                                initialValue = 0f,
                                targetValue = 1f,
                                animationSpec = tween(1000)
                            ) { value, _ ->
                                bonusTimeAnimation = value
                            }
                            delay(1000)
                            bonusTimeVisible = false
                        }
                        Text(
                            text = "+$lastPointsAdded",  // Changed from bonusTimeAnimation
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .padding(end = 8.dp)
                                .graphicsLayer(alpha = 1f - bonusTimeAnimation),
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }


            // Score at the top
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Words count with fixed width
                Box(
                    modifier = Modifier.width(150.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = "Words: $wordsCompleted",
                        fontSize = 24.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }


                // Score section with fixed width container
                Box(
                    modifier = Modifier.width(200.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        // Score text with fixed width
                        Text(
                            text = "Score: $totalScore",
                            fontSize = 24.sp,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.width(150.dp)
                        )


                        // Multiplier star with placeholder to maintain layout
                        Box(
                            modifier = Modifier.size(48.dp), // Increased from 40.dp
                            contentAlignment = Alignment.Center
                        ) {
                            if (scoreMultiplier > 1) {
                                Icon(
                                    imageVector = Icons.Filled.Star,
                                    contentDescription = "Multiplier",
                                    tint = when (scoreMultiplier) {
                                        2 -> Color(0xFF1E88E5) // Material Blue
                                        3 -> Color(0xFF43A047) // Material Green
                                        else -> Color(0xFF8E24AA) // Material Purple
                                    },
                                    modifier = Modifier.fillMaxSize()
                                )


                                Text(
                                    text = "×$scoreMultiplier",
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }


            Box(
                modifier = Modifier
                    .height(20.dp)
                    .offset(y = 2.dp)
                    .fillMaxWidth(),
                contentAlignment = Alignment.TopCenter
            ) {
                Text(
                    text = lastCompletedWord?.word?.uppercase() ?: "",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                )
            }
            // Spacer(modifier = Modifier.height(2.dp))


            // Word progress with underscores
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        repeat(maxSelectedLetters) { index ->
                            val letter = if (index < selectedLetters.length) {
                                selectedLetters[index]
                            } else {
                                null
                            }
                            LetterBox(
                                letter = letter,
                                alpha = 1f,
                                onClick = { },
                                isClickable = false,
                                wordLength = maxSelectedLetters  // Pass the word length
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Word length: $maxSelectedLetters",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }


            // Spacer(modifier = Modifier.height(2.dp))


            // Grid for letters
            LazyVerticalGrid(
                columns = GridCells.Fixed(gridSize),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)  // Added weight to make grid take remaining space
                    .padding(8.dp),  // Reduced from 16.dp
                horizontalArrangement = Arrangement.spacedBy(1.dp),  // Added small spacing
                verticalArrangement = Arrangement.spacedBy(1.dp)  // Added small spacing
            ) {
                items(totalCells) { index ->
                    val letter = letters.getOrNull(indexes.indexOf(index))


                    // Start or update timer for new letters
                    if (letter != null) {
                        LaunchedEffect(letter) {
                            val startTime = System.currentTimeMillis()
                            val totalTime = letterManager.getLetterTimer(letter)


                            while (true) {
                                val currentTime = System.currentTimeMillis()
                                val elapsedTime = currentTime - startTime
                                val remainingTime = (totalTime - elapsedTime).coerceAtLeast(0)


                                letterTimers = letterTimers.toMutableMap().apply {
                                    this[index] = remainingTime
                                }


                                if (remainingTime <= 0) {
                                    letters = letters.toMutableList().apply {
                                        this[indexes.indexOf(index)] = null
                                    }
                                    letters = letterManager.addRandomLetter(
                                        letters = letters,
                                        totalCells = totalCells,
                                        currentWord = selectedLetters,
                                        requiredLength = maxSelectedLetters
                                    )
                                    break
                                }


                                delay(50) // Update every 50ms for smooth animation
                            }
                        }
                    }
                    Box(modifier = Modifier.padding(1.dp)) {
                        LetterBox(
                            letter = letter,
                            alpha = if (letter == null) 0f else {
                                (letterTimers[index] ?: 0L).toFloat() /
                                        letterManager.getLetterTimer(letter ?: 'A')
                            },
                            onClick = {
                                if (letter != null) {
                                    val isValidLetter = selectedLetters.isEmpty() ||
                                            canFormValidWord(
                                                selectedLetters,
                                                letter,
                                                if (hardcore) availableDictionary else dictionary,
                                                maxSelectedLetters
                                            )


                                    if (!isValidLetter) {
                                        if (scoreMultiplier != 1) {
                                            soundManager.playLoseMultiplier()
                                            scoreMultiplier = 1
                                        } else {
                                            soundManager.playWrongLetter()
                                        }
                                        if (currentTime >= 1) {
                                            currentTime = (currentTime - 1f).coerceAtLeast(0f)
                                        }
                                    }


                                    if (isValidLetter) {
                                        // Add bonus time based on letter points
                                        val points = ScrabblePoints.getLetterPoints(letter)
                                        currentTime = (currentTime + points).coerceAtMost(maxTime)
                                        lastPointsAdded = points
                                        bonusTimeVisible = true


                                        soundManager.playLetterSelect()


                                        selectedLetters += letter
                                        letters = letters.toMutableList().apply {
                                            this[indexes.indexOf(index)] = null
                                        }
                                        letters = letterManager.addRandomLetter(letters, totalCells)


                                        if (selectedLetters.length >= maxSelectedLetters) {
                                            val isValidWord =
                                                selectedLetters.toLowerCase() in (if (hardcore) availableDictionary else dictionary)
                                            if (isValidWord) {
                                                wordsCompleted += 1


                                                soundManager.playWordComplete(scoreMultiplier)


                                                // Calculate word score with multiplier
                                                val baseWordScore = ScrabblePoints.calculateWordScore(
                                                    word = selectedLetters,
                                                    wordLength = selectedLetters.length,
                                                    completedWords = wordsCompleted
                                                )
                                                val finalScore = baseWordScore * scoreMultiplier
                                                totalScore += finalScore


                                                // Add word to completed list
                                                val newWordEntry = WordEntry(
                                                    word = selectedLetters,
                                                    score = finalScore,
                                                    multiplier = scoreMultiplier
                                                )
                                                completedWords = completedWords + newWordEntry
                                                lastCompletedWord = newWordEntry


                                                // In hardcore mode, remove the word from available dictionary
                                                if (hardcore) {
                                                    availableDictionary = availableDictionary.filter {
                                                        it != selectedLetters.toLowerCase()
                                                    }
                                                }


                                                // Increment multiplier (max 4)
                                                scoreMultiplier = (scoreMultiplier + 1).coerceAtMost(4)


                                                // Reset word and update parameters
                                                maxSelectedLetters = getNextWordLength()
                                                maxTime = (baseTime + maxSelectedLetters * 2f - wordsCompleted)
                                                    .coerceAtLeast(10f + maxSelectedLetters)
                                                currentTime = maxTime
                                            }
                                            selectedLetters = ""
                                        }
                                    }
                                }
                            }
                        )
                    }
                }
            }


            // Button to navigate to GameOverScreen
            Button(
                onClick = {
                    navController.navigate(
                        "gameOverScreen/${totalScore}/${wordsCompleted}/${
                            completedWords.map { "${it.word}:${it.score}:${it.multiplier}" }.joinToString(",")
                        }"
                    )
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(
                    text = "End Game",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}


@Composable
fun LetterBox(
    letter: Char?,
    alpha: Float = 1f,
    onClick: () -> Unit,
    isClickable: Boolean = true,
    wordLength: Int = 0
) {
    // Dynamic width calculation
    val boxWidth = if (!isClickable && wordLength >= 8 && wordLength < 10) {
        36.dp  // Slightly smaller for long words
    } else if (!isClickable && wordLength >= 10 && wordLength < 12) {
        30.dp
    } else if (!isClickable && wordLength == 12) {
        28.dp
    } else {
        48.dp // Original size
    }


    // Custom lerp function for colors
    fun lerpColor(start: Color, end: Color, fraction: Float): Color {
        return Color(
            red = lerp(start.red, end.red, fraction),
            green = lerp(start.green, end.green, fraction),
            blue = lerp(start.blue, end.blue, fraction),
            alpha = lerp(start.alpha, end.alpha, fraction)
        )
    }


    val backgroundColor = if (letter != null) {
        if (isClickable) {
            MaterialTheme.colorScheme.primary.copy(alpha = alpha)  // Only fade out, no color change
        } else {
            MaterialTheme.colorScheme.primary
        }
    } else {
        Color.Transparent
    }


    Box(
        modifier = Modifier
            .size(width = boxWidth, height = 48.dp)
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(4.dp)
            )
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f),
                shape = RoundedCornerShape(4.dp)
            )
            .then(if (isClickable) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        if (letter != null) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                // Calculate text color transition
                val textColor = if (isClickable) {
                    lerpColor(
                        start = MaterialTheme.colorScheme.onPrimary,
                        end = MaterialTheme.colorScheme.primary,
                        // change to Color(0xFFFF0000), to have transition to red
                        fraction = 1 - alpha
                    )
                } else {
                    MaterialTheme.colorScheme.onPrimary
                }


                // Main letter
                Text(
                    text = letter.toString(),
                    fontSize = 24.sp,
                    color = textColor,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Center)
                )


                // Points in bottom right - only shown when not clickable (selected letters)
                if (!isClickable) {
                    Text(
                        text = ScrabblePoints.getLetterPoints(letter).toString(),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .offset(x = 1.dp, y = 4.dp)
                    )
                }
            }
        } else if (!isClickable) {
            // Display underscore for empty slots in the word display
            Text(
                text = "_",
                fontSize = 32.sp,
                color = MaterialTheme.colorScheme.onBackground,
            )
        }
    }
}
