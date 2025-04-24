// SoundManager.kt
package com.example.ccl3_scrabbling

import android.content.Context
import android.media.MediaPlayer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import kotlin.random.Random

class SoundManager(private val context: Context) {
    private var letterSelectSounds: List<MediaPlayer?> = List(10) { null }
    private var wordCompleteSounds: List<MediaPlayer?> = List(4) { null }
    private var timerWarningSound: MediaPlayer? = null
    private var gameOverSound: MediaPlayer? = null
    private var loseMultiplierSound: MediaPlayer? = null
    private var wrongLetterSound: MediaPlayer? = null

    init {
        // Initialize letter select sounds
        letterSelectSounds = List(10) { index ->
            MediaPlayer.create(context, context.resources.getIdentifier(
                "letter_select_${index + 1}",
                "raw",
                context.packageName
            ))
        }

        // Initialize word complete sounds for different multipliers
        wordCompleteSounds = List(4) { index ->
            MediaPlayer.create(context, context.resources.getIdentifier(
                "word_complete_${index + 1}",
                "raw",
                context.packageName
            ))
        }

        timerWarningSound = MediaPlayer.create(context, R.raw.timer_warning)
        gameOverSound = MediaPlayer.create(context, R.raw.game_over)
        loseMultiplierSound = MediaPlayer.create(context, R.raw.lose_multiplier)
        wrongLetterSound = MediaPlayer.create(context, R.raw.wrong_letter)

        timerWarningSound?.isLooping = true
    }

    fun playLetterSelect() {
        val randomIndex = Random.nextInt(letterSelectSounds.size)
        letterSelectSounds[randomIndex]?.let {
            if (!it.isPlaying) {
                it.seekTo(0)
                it.start()
            }
        }
    }

    fun playWordComplete(multiplier: Int) {
        // multiplier is 1-4, so we subtract 1 for 0-based index
        val soundIndex = (multiplier - 1).coerceIn(0, 3)
        wordCompleteSounds[soundIndex]?.let {
            if (!it.isPlaying) {
                it.seekTo(0)
                it.start()
            }
        }
    }

    fun playTimerWarning() {
        timerWarningSound?.let {
            if (!it.isPlaying) {
                it.start()
            }
        }
    }

    fun stopTimerWarning() {
        timerWarningSound?.let {
            if (it.isPlaying) {
                it.pause()
                it.seekTo(0)
            }
        }
    }

    fun playGameOver() {
        gameOverSound?.let {
            if (!it.isPlaying) {
                it.start()
            }
        }
    }

    fun playWrongLetter() {
        wrongLetterSound?.let {
            if (!it.isPlaying) {
                it.start()
            }
        }
    }

    fun playLoseMultiplier() {
        loseMultiplierSound?.let {
            if (!it.isPlaying) {
                it.start()
            }
        }
    }

    fun release() {
        letterSelectSounds.forEach { it?.release() }
        wordCompleteSounds.forEach { it?.release() }
        timerWarningSound?.release()
        gameOverSound?.release()

        letterSelectSounds = List(10) { null }
        wordCompleteSounds = List(4) { null }
        timerWarningSound = null
        gameOverSound = null
    }
}

@Composable
fun rememberSoundManager(): SoundManager {
    val context = LocalContext.current
    val soundManager = remember { SoundManager(context) }

    DisposableEffect(soundManager) {
        onDispose {
            soundManager.release()
        }
    }

    return soundManager
}