package com.siddevs.reverb.ui.analysis

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp
import io.wavebeans.lib.BeanStream
import io.wavebeans.lib.io.out
import io.wavebeans.lib.stream.fft.FftSample
import io.wavebeans.lib.stream.trim
import java.lang.Float.min
import kotlin.math.absoluteValue

fun drawSpectrogram(drawScope: DrawScope, fft: BeanStream<FftSample>,size: Size){
    val sequenceItr = fft.trim(3000).asSequence(sampleRate = 44100F).iterator()
    var count = 0F
    while (sequenceItr.hasNext()){
        val currentSample = sequenceItr.next()
        val magnitudeItr = currentSample.magnitude().iterator()
        for (freq in currentSample.frequency()){
            val y1 = freq.toFloat() / 30F + 2F
            val y2 = y1 + 2
            val magnitude = magnitudeItr.next().toFloat().absoluteValue / 30F
            drawScope.drawLine(color = Color.White.copy(alpha = min(magnitude,1F)), start = Offset(count,y1), end = Offset(count, y2), strokeWidth = 1F)
        }
        count+=4
    }

}


@Composable
fun Spectrogram(fft: BeanStream<FftSample>, isLightMode: Boolean, height:Float) {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp),
        onDraw = {
            drawRect(color = if(isLightMode ) Color.White else Color.Black, size = Size(width=size.width, height = height) )
            drawSpectrogram(this, fft,size)
        },
    )
}