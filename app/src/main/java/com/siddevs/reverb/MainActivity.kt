package com.siddevs.reverb

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import com.siddevs.reverb.ui.home.MainPage
import com.siddevs.reverb.ui.theme.ReverbTheme
import com.siddevs.reverb.utility.AudioRecorder


class MainActivity : ComponentActivity() {
    lateinit var audioRecorder: AudioRecorder;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val fileNameAudio = filesDir.path + "/testfile" + ".pcm"
        Log.d("MainActivity","fileDir $fileNameAudio");
        audioRecorder =AudioRecorder(fileName = fileNameAudio,this,this);
        // check for permission
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), 200)
        }
        setContent {
            ReverbTheme {
                // A surface container using the 'background' color
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    MainPage(audioRecorder = audioRecorder)
                }
            }
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 200 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permission to record audio granted", Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(this, "Permission to record audio denied", Toast.LENGTH_LONG).show();
        }
    }
}

