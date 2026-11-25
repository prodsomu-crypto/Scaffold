package org.studiotouch.app

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlin.math.PI
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.pow

/**
 * Small starter oscillator for audible note previews.
 * This is deliberately a preview voice, not yet a multi-track DAW audio engine.
 */
object AudioEngine {
    private const val SAMPLE_RATE = 44_100
    private const val AMPLITUDE = 0.22

    fun previewMidi(noteMidi: Int, durationMs: Int = 350) {
        if (noteMidi !in 0..127) return

        val safeDuration = durationMs.coerceIn(80, 1500)
        val frameCount = SAMPLE_RATE * safeDuration / 1000
        val frequency = 440.0 * 2.0.pow((noteMidi - 69) / 12.0)
        val releaseSeconds = 0.06
        val pcm = ShortArray(frameCount) { frame ->
            val time = frame.toDouble() / SAMPLE_RATE
            val attack = (time / 0.008).coerceIn(0.0, 1.0)
            val remaining = safeDuration / 1000.0 - time
            val release = (remaining / releaseSeconds).coerceIn(0.0, 1.0)
            val envelope = min(attack, release)
            (sin(2.0 * PI * frequency * time) * AMPLITUDE * envelope * Short.MAX_VALUE)
                .toInt().toShort()
        }

        Thread({
            var track: AudioTrack? = null
            try {
                val attributes = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
                val format = AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(SAMPLE_RATE)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()

                track = AudioTrack.Builder()
                    .setAudioAttributes(attributes)
                    .setAudioFormat(format)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .setBufferSizeInBytes(pcm.size * 2)
                    .build()

                val written = track.write(pcm, 0, pcm.size)
                if (written > 0) {
                    track.play()
                    Thread.sleep(safeDuration.toLong() + 80L)
                }
            } catch (_: InterruptedException) {
                Thread.currentThread().interrupt()
            } catch (_: Exception) {
                // Audio preview should never crash the composing UI.
            } finally {
                try {
                    track?.release()
                } catch (_: Exception) {
                    // Ignore cleanup failures from an already-released audio device.
                }
            }
        }, "StudioTouch-note-preview").start()
    }
}
