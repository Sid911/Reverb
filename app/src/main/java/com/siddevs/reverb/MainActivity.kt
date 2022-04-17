package com.siddevs.reverb

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Vibrator
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.siddevs.reverb.ui.analysis.AnalysisPage
import com.siddevs.reverb.ui.home.MainPage
import com.siddevs.reverb.ui.home.Waveform
import com.siddevs.reverb.ui.theme.Grey50
import com.siddevs.reverb.ui.theme.ReverbTheme
import com.siddevs.reverb.utility.AudioRecorder
import com.siddevs.reverb.utility.AudioRecorderM
import com.siddevs.reverb.utility.Timer


class MainActivity : ComponentActivity(), Timer.OnTimerTickListener {
        private lateinit var audioRecorder: AudioRecorder
    private lateinit var mediaRecorder: AudioRecorderM
    private lateinit var timer: Timer
    private lateinit var vibrator: Vibrator

    private var amplitudes = mutableStateListOf<Float>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val fileNameAudio = filesDir.path + "/testfile" + ".pcm"
        val fileNameAudio2 = filesDir.path + "/testfile2" + ".3gp"
        Log.d("MainActivity", "fileDir $fileNameAudio");
        audioRecorder = AudioRecorder(filepath = fileNameAudio,this,this)
        mediaRecorder = AudioRecorderM(fileName = fileNameAudio2)

        timer = Timer(this)
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
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
                    ReverbApp(
                        audioRecorder= audioRecorder,
                        mediaRecorder = mediaRecorder,
                        timer = timer,
                        vibrator = vibrator,
                        amplitudes = amplitudes
                    )
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
            Toast.makeText(this, "Permission to record audio granted", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, "Permission to record audio denied", Toast.LENGTH_LONG).show()
        }
    }

    override fun onTimerTick(duration: String) {
//        println("$duration : ${audioRecorder.amplitude}");
//        audioRecorder.amplitude = 0
        mediaRecorder.recorder?.let {
            amplitudes.add(it.maxAmplitude.toFloat());
        }
    }
}


@Composable
fun ReverbApp(
    audioRecorder: AudioRecorder,
    mediaRecorder: AudioRecorderM,
    timer: Timer,
    vibrator: Vibrator,
    amplitudes: SnapshotStateList<Float>
) {
    val navController: NavHostController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            Column {
                MainPage(
                    audioRecorder = audioRecorder,
                    mediaRecorder = mediaRecorder,
                    timer = timer,
                    vibrator = vibrator,
                    onStop = { navController.navigate("analysis"); amplitudes.clear() })
                Waveform(array = amplitudes.toTypedArray())
                FloatingActionButton(onClick = { navController.navigate("analysis") }, shape = RoundedCornerShape(size = 20.dp), backgroundColor = Grey50) {
                    
                }
            }
        }
        composable("analysis") { AnalysisPage(navHostController = navController, audioRecorder = audioRecorder) }
        /*...*/
    }
}

