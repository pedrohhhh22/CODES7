package com.appsdevs.popit

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import android.util.Log

object SoundManager {
    private const val TAG = "SoundManager"

    private var soundPool: SoundPool?  = null
    private var isInitialized = false

    // Volume controls (0.0f to 1.0f)
    private var sfxVolume = 1.0f
    private var isSfxMuted = false

    // Sound IDs
    private var bubblePopId = 0
    private var countdownId = 0
    private var goId = 0
    private var difficultyId = 0
    private var coinEarnId = 0
    private var luxDropId = 0
    private var lostStreakId = 0
    private var x3StreakId = 0
    private var x10StreakId = 0
    private var x20StreakId = 0
    private var x30StreakId = 0
    private var x50StreakId = 0

    fun init(context: Context) {
        if (isInitialized) return

        try {
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()

            soundPool = SoundPool.Builder()
                .setMaxStreams(8)
                .setAudioAttributes(audioAttributes)
                .build()

            soundPool?.let { sp ->
                bubblePopId = loadSoundSafe(sp, context, R.raw.bubblepop)
                countdownId = loadSoundSafe(sp, context, R. raw.countdown)
                goId = loadSoundSafe(sp, context, R.raw.go)
                difficultyId = loadSoundSafe(sp, context, R.raw.difficulty)
                coinEarnId = loadSoundSafe(sp, context, R.raw.coinearn)
                luxDropId = loadSoundSafe(sp, context, R.raw.luxdrop)
                lostStreakId = loadSoundSafe(sp, context, R.raw.loststreak)
                x3StreakId = loadSoundSafe(sp, context, R. raw.x3streak)
                x10StreakId = loadSoundSafe(sp, context, R.raw.x10streak)
                x20StreakId = loadSoundSafe(sp, context, R.raw.x20streak)
                x30StreakId = loadSoundSafe(sp, context, R.raw.x30streak)
                x50StreakId = loadSoundSafe(sp, context, R.raw. x50streak)
            }

            isInitialized = true
            Log.d(TAG, "SoundManager initialized successfully")
        } catch (e: Exception) {
            Log. e(TAG, "Failed to initialize SoundManager: ${e. message}")
        }
    }

    private fun loadSoundSafe(sp: SoundPool, context: Context, resId: Int): Int {
        return try {
            sp. load(context, resId, 1)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load sound resource: $resId - ${e.message}")
            0
        }
    }

    private fun play(soundId: Int, volume: Float = 1.0f) {
        if (isSfxMuted || soundId == 0) return

        try {
            val finalVolume = (volume * sfxVolume).coerceIn(0f, 1f)
            soundPool?.play(soundId, finalVolume, finalVolume, 1, 0, 1.0f)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to play sound: ${e.message}")
        }
    }

    // ==================== BUBBLE SOUNDS ====================

    fun playBubblePop() = play(bubblePopId)

    // ==================== COUNTDOWN SOUNDS ====================

    fun playCountdown() = play(countdownId)

    fun playGo() = play(goId)

    // ==================== GAME EVENT SOUNDS ====================

    fun playDifficulty() = play(difficultyId, 0.7f)

    fun playCoinEarn() = play(coinEarnId)

    fun playLuxDrop() = play(luxDropId)

    fun playLostStreak() = play(lostStreakId, 0.6f)

    // ==================== STREAK SOUNDS ====================

    fun playStreakCombo(streak: Int) {
        if (streak >= 3 && streak % 3 == 0) {
            play(x3StreakId)
        }
    }

    fun playStreakMilestone(streak: Int) {
        when (streak) {
            10 -> play(x10StreakId)
            20 -> play(x20StreakId)
            30 -> play(x30StreakId)
            50 -> play(x50StreakId)
        }
    }

    // ==================== VOLUME CONTROL ====================

    fun setSfxVolume(volume: Float) {
        sfxVolume = volume.coerceIn(0f, 1f)
    }

    fun getSfxVolume(): Float = sfxVolume

    fun setSfxMuted(muted: Boolean) {
        isSfxMuted = muted
    }

    fun isSfxMuted(): Boolean = isSfxMuted

    // ==================== CLEANUP ====================

    fun release() {
        try {
            soundPool?.release()
        } catch (e:  Exception) {
            Log.e(TAG, "Failed to release SoundPool: ${e.message}")
        }
        soundPool = null
        isInitialized = false

        bubblePopId = 0
        countdownId = 0
        goId = 0
        difficultyId = 0
        coinEarnId = 0
        luxDropId = 0
        lostStreakId = 0
        x3StreakId = 0
        x10StreakId = 0
        x20StreakId = 0
        x30StreakId = 0
        x50StreakId = 0
    }
}