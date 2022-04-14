package com.siddevs.reverb.ui.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp
import com.siddevs.reverb.ui.theme.BlueA100

fun drawWaves(drawScope: DrawScope, array: Array<Float>, size: Size) {
    val leftPadding = 0f
    val topPadding = 0F
    val spacing = 10F
    val strokeWidth = 5F
    val maxPossibleWidth = (size.width / (spacing + strokeWidth)).toInt()
    val arr = array.takeLast(maxPossibleWidth)
    for (i in arr.indices) {
        val norm = (arr[i] / 40).coerceAtMost(400F)
        val x1 = leftPadding + (spacing + strokeWidth) * i
        val y1 = topPadding + 400 - norm / 2

        drawScope.drawRoundRect(
            color = BlueA100,
            topLeft = Offset(x1, y1),
            size = Size(strokeWidth, norm),
            cornerRadius = CornerRadius(2F, 2F)
        )
    }
}

@Composable
fun Waveform(array: Array<Float>) {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp),
        onDraw = {
            drawWaves(this, array = array, size = size);
        },
    )
}