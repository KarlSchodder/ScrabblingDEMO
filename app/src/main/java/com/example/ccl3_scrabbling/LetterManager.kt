package com.example.ccl3_scrabbling

import kotlin.random.Random
import com.example.ccl3_scrabbling.data.dictionary

class LetterManager {
    // Existing letter frequencies and timers remain the same
    private val letterFrequencies = mapOf(
        'E' to 12,
        'A' to 9, 'I' to 9, 'O' to 9,
        'N' to 6, 'R' to 6, 'T' to 6,
        'L' to 4, 'S' to 4, 'U' to 4, 'D' to 4, 'G' to 4,
        'B' to 2, 'C' to 2, 'M' to 2, 'P' to 2, 'F' to 2,
        'H' to 2, 'V' to 2, 'W' to 2, 'Y' to 2,
        'K' to 1, 'J' to 1, 'X' to 1, 'Q' to 1, 'Z' to 1
    )

    private val letterTimers = mapOf(
        'E' to 4000L, 'A' to 4000L, 'I' to 4000L, 'O' to 4000L, 'N' to 4000L, 'R' to 4000L,
        'T' to 4000L, 'L' to 4000L, 'S' to 4000L, 'U' to 4000L, 'D' to 4000L, 'G' to 4000L,
        'B' to 6000L, 'C' to 6000L, 'M' to 6000L, 'P' to 6000L, 'F' to 6000L, 'H' to 6000L,
        'V' to 7000L, 'W' to 7000L, 'Y' to 7000L, 'K' to 7000L,
        'J' to 9000L, 'X' to 9000L,
        'Q' to 11000L, 'Z' to 11000L
    )

    fun getLetterTimer(letter: Char): Long {
        return letterTimers[letter] ?: 4000L
    }

    // New method to find valid next letters
    private fun findValidNextLetters(currentWord: String, requiredLength: Int): Set<Char> {
        if (currentWord.length >= requiredLength) return emptySet()

        return dictionary
            .filter { word ->
                word.length == requiredLength &&
                        word.startsWith(currentWord.toLowerCase())
            }
            .map { it[currentWord.length] }
            .toSet()
            .map { it.uppercaseChar() }
            .toSet()
    }

    // Modified getRandomLetter to potentially return a valid letter
    fun getRandomLetter(currentWord: String = "", requiredLength: Int = 4): Char {
        // If there's a current word, check for valid next letters
        if (currentWord.isNotEmpty()) {
            val validLetters = findValidNextLetters(currentWord, requiredLength)

            // If we have fewer than 3 valid letters and 10% chance hits
            if (validLetters.size < 3 && validLetters.isNotEmpty() && Random.nextDouble() < 0.1) {
                return validLetters.random()
            }
        }

        // Default to normal random letter generation
        val totalFrequency = letterFrequencies.values.sum()
        val randomValue = Random.nextInt(totalFrequency)
        var cumulativeFrequency = 0

        for ((letter, frequency) in letterFrequencies) {
            cumulativeFrequency += frequency
            if (randomValue < cumulativeFrequency) {
                return letter
            }
        }
        return 'E'
    }

    // Updated to include current word and required length
    fun generateRandomLetters(count: Int, currentWord: String = "", requiredLength: Int = 4): List<Char> {
        return List(count) { getRandomLetter(currentWord, requiredLength) }
    }

    fun generateInitialLetters(totalCells: Int, initialCount: Int): List<Char?> {
        val letters = generateRandomLetters(initialCount).map { it as Char? }
        return letters + List(totalCells - initialCount) { null }
    }

    // Updated to include current word and required length
    fun addRandomLetter(
        letters: List<Char?>,
        totalCells: Int,
        currentWord: String = "",
        requiredLength: Int = 4
    ): List<Char?> {
        val newLetters = letters.toMutableList()
        val emptyIndexes = (0 until totalCells).filter { newLetters.getOrNull(it) == null }
        if (emptyIndexes.isNotEmpty()) {
            val randomIndex = emptyIndexes.random()
            newLetters[randomIndex] = getRandomLetter(currentWord, requiredLength)
        }
        return newLetters
    }
}