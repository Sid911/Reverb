package com.siddevs.reverb.ui.analysis

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp

fun drawSpectrogram(drawScope: DrawScope){


}


@Composable
fun Spectrogram(){
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp),
        onDraw = {
            drawSpectrogram(this)
        },
    )
}