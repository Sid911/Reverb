package com.siddevs.reverb.ui.home

import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.siddevs.reverb.ui.theme.*
import com.siddevs.reverb.utility.AudioRecorder
import com.siddevs.reverb.utility.AudioRecorderM
import com.siddevs.reverb.utility.Timer

@Composable
fun MainPage(audioRecorder: AudioRecorderM, timer: Timer, vibrator: Vibrator, onStop: ()->Unit) {
    val isLightTheme = MaterialTheme.colors.isLight;
    var click by remember { mutableStateOf(false) };
    val scale: Float by animateFloatAsState(if (click) 2f else 1f)
    var title by remember { mutableStateOf("Reverb")}
    Column(
        modifier = Modifier.padding(horizontal = Dp(20f), vertical = Dp(30f)),
    ) {

        Row(horizontalArrangement = Arrangement.Start) {
            Text(
                text = title,
                style = TextStyle(fontSize = 30.sp, fontWeight = FontWeight.Bold)
            );
        }
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 100.dp),
        ) {
            Button(
                onClick = {
                    if(click) {
//                        audioRecorder.continueRecording = false;
                        audioRecorder.stopRecording();
                        timer.stop()
                        audioRecorder.startPlaying();
                        title = "Reverb"

                        onStop()
                    } else {
                        timer.start()
                        audioRecorder.startRecording()
                        title = "Reverb :P"
                    }
                    vibrator.vibrate(VibrationEffect.createOneShot(50,VibrationEffect.DEFAULT_AMPLITUDE))
                    click = !click ;
                },
                colors = ButtonDefaults.buttonColors(backgroundColor = if(click) BlueA100 else{ if (isLightTheme) Grey900 else Grey200}),
                shape = CircleShape,
                elevation = ButtonDefaults.elevation(
                    defaultElevation = 4.dp,
                    pressedElevation = 12.dp
                ),
                contentPadding = PaddingValues(all = 20.dp),
                modifier = androidx.compose.ui.Modifier
                    .width(200.dp)
                    .height(200.dp),
            ) {
                Icon(
                    if(click)Icons.Filled.Close else Icons.Outlined.PlayArrow, contentDescription = "Record",
                    tint = if (isLightTheme)
                        Color.White
                    else
                        Color.Black,
                    modifier = Modifier.scale(scale)
                )
            };
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}