package com.siddevs.reverb.ui.analysis

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.siddevs.reverb.utility.AudioRecorder


@Composable
fun AnalysisPage(navHostController: NavHostController,audioRecorder: AudioRecorder) {
    val isLightTheme = MaterialTheme.colors.isLight;
    Column() {
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
        Text(text = "This is analysis Page", modifier= Modifier.padding(20.dp))

        Spectrogram(audioRecorder.getFFT(), isLightMode = isLightTheme, height = 800F)
    }
}