package com.example.tosswidget.logic

import android.content.Context
import android.media.AudioManager
import kotlin.random.Random

enum class TossResult {
    HEADS, TAILS
}

object TossLogic {
    fun performToss(context: Context): TossResult {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)

        // Calculate volume percentage
        val percentage = if (maxVolume > 0) (currentVolume.toFloat() / maxVolume.toFloat()) else 0f

        return when {
            percentage >= 1.0f -> TossResult.HEADS
            percentage <= 0.0f -> TossResult.TAILS
            else -> if (Random.nextBoolean()) TossResult.HEADS else TossResult.TAILS
        }
    }
}
