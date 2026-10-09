package com.quickmindgames.sudoku.data.util

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.VibrationEffect
import android.os.Vibrator
import com.quickmindgames.sudoku.R

class GameFeedback(context: Context) {

    private val lock = Any()
    private val soundPool = SoundPool.Builder()
        .setMaxStreams(2)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        )
        .build()
    private val vibrator = context.applicationContext.getSystemService(Vibrator::class.java)
    private val loadedSounds = mutableSetOf<Int>()
    private val pendingSounds = mutableListOf<Int>()
    private var released = false

    private val correctSound: Int
    private val wrongSound: Int
    private val completedSound: Int
    private val gameOverSound: Int

    init {
        soundPool.setOnLoadCompleteListener { pool, sampleId, status ->
            synchronized(lock) {
                if (status == 0 && !released) {
                    loadedSounds.add(sampleId)
                    val pendingCount = pendingSounds.count { it == sampleId }
                    repeat(pendingCount) {
                        pool.play(sampleId, 1f, 1f, 1, 0, 1f)
                    }
                }
                pendingSounds.removeAll { it == sampleId }
            }
        }
        correctSound = soundPool.load(context.applicationContext, R.raw.number_correct, 1)
        wrongSound = soundPool.load(context.applicationContext, R.raw.number_wrong, 1)
        completedSound = soundPool.load(context.applicationContext, R.raw.puzzle_complete, 1)
        gameOverSound = soundPool.load(context.applicationContext, R.raw.game_over, 1)
    }

    fun playCorrectNumber(enabled: Boolean) {
        if (enabled) playSound(correctSound)
    }

    fun playWrongNumber(enabled: Boolean) {
        if (enabled) {
            playSound(wrongSound)
            vibrate(durationMillis = 100, amplitude = 156)
        }
    }

    fun playPuzzleCompleted(enabled: Boolean) {
        if (enabled) {
            playSound(completedSound)
            vibrate(durationMillis = 35, amplitude = 64)
        }
    }

    fun playGameOver(enabled: Boolean){
        if (enabled) {
            playSound(gameOverSound)
            vibrate(durationMillis = 35, amplitude = 64)
        }
    }

    fun release() {
        synchronized(lock) {
            if (released) return
            released = true
            pendingSounds.clear()
            soundPool.release()
        }
    }

    private fun playSound(soundId: Int) {
        synchronized(lock) {
            if (released) return
            if (soundId in loadedSounds) {
                soundPool.play(soundId, 1f, 1f, 1, 0, 1f)
            } else {
                pendingSounds.add(soundId)
            }
        }
    }

    private fun vibrate(durationMillis: Long, amplitude: Int) {
        vibrator
            ?.takeIf { it.hasVibrator() }
            ?.vibrate(VibrationEffect.createOneShot(durationMillis, amplitude))
    }
}
