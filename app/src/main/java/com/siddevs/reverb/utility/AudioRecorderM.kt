package com.siddevs.reverb.utility

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.MediaRecorder
import androidx.core.app.ComponentActivity
import java.io.IOException


class AudioRecorderM(private val fileName: String, private val context: Context, private val activity: ComponentActivity) {
    var recorder: MediaRecorder? = null

    fun startRecording() {
        // initialize and configure MediaRecorder
        recorder = MediaRecorder()
        recorder!!.setAudioSource(MediaRecorder.AudioSource.MIC)
        recorder!!.setOutputFile(fileName)
        recorder!!.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
        recorder!!.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        recorder!!.setAudioSamplingRate(44100)
        try {
            recorder!!.prepare()
        } catch (e: IOException) {

        } catch (e: IllegalStateException) {
            // handle error
        }
        recorder!!.start()
    }

    fun stopRecording() {
        // stop recording and free up resources
        recorder!!.stop()
        recorder!!.release()
        recorder = null
    }


    private var player: MediaPlayer? = null


     fun startPlaying() {
        player = MediaPlayer()
        try {
            player!!.setDataSource(fileName) // pass reference to file to be played
            player!!.setAudioAttributes(
                AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            ) // optional step
            player!!.prepare() // may take a while depending on the media, consider using .prepareAsync() for streaming
        } catch (e: IOException) { // we need to catch both errors in case of invalid or inaccessible resources
            // handle error
        } catch (e: IllegalArgumentException) {
            // handle error
        }
        player!!.start()
         player!!.setOnCompletionListener {
             stopPlaying()
         }
    }

    private fun stopPlaying() {
        player!!.stop()
        player!!.release() // free up resources
        player = null
    }
}