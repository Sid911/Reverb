package com.siddevs.reverb.utility

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.*

import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.ComponentActivity
import java.io.*


class AudioRecorder(val fileName: String,val context: Context, val activity: ComponentActivity) {
    private val TAG = "AudioRecorder";

    init {
        val fileAudio = File(fileName)
        if (fileAudio.exists()) {
            fileAudio.delete();
        }
        try {
            fileAudio.createNewFile()
        } catch (e: IOException) {
            Log.d(TAG, "could not create file $e")
            e.printStackTrace()
        }
    }

    // status booleans
    var continueRecording = true
    private var isRecordingAudio = false
    private var isPlayingAudio = false

    // audio stuff
    private var recordingThread: Thread? = null
    private var playingThread: Thread? = null

    private val AUDIO_SOURCE =
        MediaRecorder.AudioSource.UNPROCESSED // for raw audio, use MediaRecorder.AudioSource.UNPROCESSED, see note in MediaRecorder section

    private val SAMPLE_RATE = 44100
    private val CHANNEL_CONFIG: Int = AudioFormat.CHANNEL_IN_MONO
    private val AUDIO_FORMAT: Int = AudioFormat.ENCODING_PCM_8BIT
    private val BUFFER_SIZE_RECORDING =
        AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT)

    var audioRecord: AudioRecord? = null

    fun startRecording() {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.RECORD_AUDIO), 200)
            return
        }
        Log.d(TAG, "The buffer size is : $BUFFER_SIZE_RECORDING");
        audioRecord = AudioRecord(
            AUDIO_SOURCE,
            SAMPLE_RATE,
            CHANNEL_CONFIG,
            AUDIO_FORMAT,
            BUFFER_SIZE_RECORDING
        )
        if (audioRecord!!.state != AudioRecord.STATE_INITIALIZED) { // check for proper initialization
            Log.e(TAG, "error initializing ")
            return
        }
        audioRecord!!.startRecording()
        Log.d("Main", "recording started with AudioRecord");

        isRecordingAudio = true
        continueRecording = true

        recordingThread = Thread { writeAudioData() }
        recordingThread!!.start()
    }


    private fun writeAudioData() {
        // to be called in a Runnable for a Thread created after call to startRecording()
        val data =
            ByteArray(BUFFER_SIZE_RECORDING / 2) // assign size so that bytes are read in in chunks inferior to AudioRecord internal buffer size
        var outputStream: FileOutputStream? = null
        try {
            outputStream =
                FileOutputStream(fileName) //fileName is path to a file, where audio data should be written
        } catch (e: FileNotFoundException) {
            // handle error
        }
        while (continueRecording) { // continueRecording can be toggled by a button press, handled by the main (UI) thread
            val read = audioRecord!!.read(data, 0, data.size)
            try {
                outputStream?.write(data, 0, read)
            } catch (e: IOException) {
                Log.d(TAG, "exception while writing to file")
                e.printStackTrace()
            }
        }
        try {
            outputStream?.flush()
            outputStream?.close()
        } catch (e: IOException) {
            Log.d("Main", "exception while closing output stream $e")
            e.printStackTrace()
        }
        isRecordingAudio = false;
        // Clean up
        audioRecord!!.stop()
        audioRecord!!.release()
        audioRecord = null
    }


    private var audioTrack: AudioTrack? = null
    private val BUFFER_SIZE_PLAYING =
        AudioTrack.getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_OUT_MONO, AUDIO_FORMAT)

    fun startPlaying() {
        val audioAttributes = AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_UNKNOWN) // defines the type of content being played
            .setUsage(AudioAttributes.USAGE_MEDIA) // defines the purpose of why audio is being played in the app
            .build()
        val audioFormat = AudioFormat.Builder()
            .setEncoding(AudioFormat.ENCODING_PCM_8BIT) // we plan on reading byte arrays of data, so use the corresponding encoding
            .setSampleRate(SAMPLE_RATE)
            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
            .build()
        audioTrack = AudioTrack(
            audioAttributes,
            audioFormat,
            BUFFER_SIZE_PLAYING,
            AudioTrack.MODE_STREAM,
            AudioManager.AUDIO_SESSION_ID_GENERATE
        )

        if (audioTrack!!.state != AudioTrack.STATE_INITIALIZED) {
            Toast.makeText(
                context,
                "Couldn't initialize AudioTrack, check configuration",
                Toast.LENGTH_SHORT
            ).show()
            Log.e("Main", "error initializing AudioTrack")
            return
        }

        audioTrack!!.play()
        Log.d("Main", "playback started with AudioTrack")

        isPlayingAudio = true

        playingThread = Thread { readAudioData() }
        playingThread!!.start()
    }

    private fun readAudioData() {
        // small buffer size to not overflow AudioTrack's internal buffer
        val data = ByteArray(BUFFER_SIZE_PLAYING / 2)
        var fileInputStream: FileInputStream? = null
        try {
            fileInputStream = FileInputStream(File(fileName))
        } catch (e: IOException) {
            Log.d(TAG, "Error while reading data")
        }
        var i = 0
        while (i != -1) { // run until file ends
            try {
                i = fileInputStream!!.read(data)
                audioTrack!!.write(data, 0, i)
            } catch (e: IOException) {
                // handle exception
            }
        }
        try {
            fileInputStream!!.close()
        } catch (e: IOException) {
            // handle exception
        }
        isPlayingAudio = false;
        audioTrack!!.stop()
        audioTrack!!.release()
        audioTrack = null
    }
}