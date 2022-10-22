package com.siddevs.reverb.ui.analysis

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp
import com.siddevs.reverb.ui.theme.BlueA100
import com.siddevs.reverb.ui.theme.Grey200
import io.wavebeans.lib.stream.fft.FftSample
import kotlin.math.absoluteValue
import kotlin.math.min

fun drawSpectrogram(drawScope: DrawScope, fft: List<FftSample>, height: Float, defaultColor: Color) {
    val sequenceItr = fft.iterator()
    var count = 0F
    while (sequenceItr.hasNext()) {
        val currentSample = sequenceItr.next()
        val magnitudeItr = currentSample.magnitude().iterator()
        for (freq in currentSample.frequency()) {
            val y1 = height - (freq.toFloat() / 30F)
            val y2 = y1 - 1
            val magnitude = magnitudeItr.next()
            val alpha = min(magnitude.absoluteValue / 50F, 1f.toDouble()).toFloat()
            val color :Color = if(magnitude < 0){
                Color.Yellow.copy(alpha = min(alpha*3, 1F) )
            } else if (magnitude < 25){
                BlueA100.copy(alpha = alpha)
            } else {
                defaultColor.copy(alpha = alpha)
            }
            drawScope.drawLine(
                color = color,
                start = Offset(count, y1),
                end = Offset(count, y2),
                strokeWidth = 1F
            )
        }
        count += 2F
    }
}


@Composable
fun Spectrogram(fft: List<FftSample>, isLightMode: Boolean, height: Float) {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .offset(y = 20.dp),
        onDraw = {
            drawRect(
                color = if (isLightMode) Grey200 else Color.Black,
                size = Size(width = size.width, height = height)
            )
            drawSpectrogram(this, fft, height, if (isLightMode) Color.Black else Color.White)
        },
    )
}