package com.example.blindfoldchess.ui.play

import android.content.Context
import android.content.SharedPreferences
import android.media.AudioAttributes
import android.media.SoundPool
import com.example.blindfoldchess.R

class SoundManager(private val context: Context) {

    private val soundPool: SoundPool
    private val soundMap = mutableMapOf<String, Int>()

    init {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(5)
            .setAudioAttributes(audioAttributes)
            .build()

        soundMap["move"] = soundPool.load(context, R.raw.move, 1)
        soundMap["capture"] = soundPool.load(context, R.raw.capture, 1)
        soundMap["check"] = soundPool.load(context, R.raw.check, 1)
        soundMap["victory"] = soundPool.load(context, R.raw.victory, 1)
        soundMap["defeat"] = soundPool.load(context, R.raw.defeat, 1)
        soundMap["draw"] = soundPool.load(context, R.raw.draw, 1)
    }

    fun playSound(key: String) {
        soundMap[key]?.let { soundId ->
            soundPool.play(soundId, 1f, 1f, 1, 0, 1f)
        }
    }

    fun release() {
        soundPool.release()
    }
}