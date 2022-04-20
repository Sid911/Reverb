package com.siddevs.reverb.ui.analysis

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.siddevs.reverb.utility.AudioRecorder
import io.wavebeans.lib.stream.fft.FftSample
import java.util.concurrent.LinkedBlockingQueue

@Composable
fun AnalysisPage(navHostController: NavHostController,audioRecorder: AudioRecorder) {
    val queue = LinkedBlockingQueue<List<FftSample>>()
    var render by remember {
        mutableStateOf(false)
    }
    val fftThread = Thread {
        queue.add(audioRecorder.getFFT())
        render = true
    }
    fftThread.start()
    val isLightTheme = MaterialTheme.colors.isLight
    Column{
        IconButton(onClick = { navHostController.popBackStack() }) {
            Icon(
                Icons.Outlined.ArrowBack, contentDescription = "Record",
                tint = if (isLightTheme)
                    Color.Black
                else
                    Color.White,
                modifier = Modifier.scale(1F)
            )
        }
        Text(text = "Audio Filepath = ${audioRecorder.filepath}", modifier= Modifier.padding(10.dp))
        Text(text = "Noise at 20Khz = 2.3dB", modifier= Modifier.padding(10.dp))
        if(render)Spectrogram(queue.take(), isLightMode = isLightTheme, height = 750F)
    }
}