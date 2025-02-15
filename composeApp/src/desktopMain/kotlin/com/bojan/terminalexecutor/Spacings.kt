package com.bojan.terminalexecutor

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.bojan.terminalexecutor.utils.clamp

val spacing_xxxs = 1.dp
val spacing_xxs = 2.dp
val spacing_xs = 4.dp
val spacing_s = 8.dp
val spacing_m = 16.dp
val spacing_l = 24.dp
val spacing_xl = 32.dp
val spacing_xxl = 40.dp
val spacing_xxxl = 64.dp

@Composable
fun DraggableVerticalSpacer(size: Dp, maxOffset: Float = 0.0f, defaultOffset: Float, onDragOffset: (Float) -> Unit) {
    var offsetY = defaultOffset
    Box(
        modifier = Modifier
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    if (maxOffset > 0) {
                        offsetY = (offsetY + dragAmount.y).clamp(maxOffset * -1, maxOffset)
                    } else {
                        offsetY += dragAmount.y
                    }
                    onDragOffset(offsetY)
                    change.consume()
                }
            }
    ) {
        Spacer(modifier = Modifier.height(size).fillMaxWidth())
    }
}

@Composable
fun VerticalSpacer_xs() {
    Spacer(modifier = Modifier.height(spacing_xs))
}

@Composable
fun VerticalSpacer_s() {
    Spacer(modifier = Modifier.height(spacing_s))
}

@Composable
fun VerticalSpacer_m() {
    Spacer(modifier = Modifier.height(spacing_m))
}

@Composable
fun VerticalSpacer_l() {
    Spacer(modifier = Modifier.height(spacing_l))
}

@Composable
fun HorizontalSpacer_xs() {
    Spacer(modifier = Modifier.width(spacing_xs))
}

@Composable
fun HorizontalSpacer_s() {
    Spacer(modifier = Modifier.width(spacing_s))
}

@Composable
fun HorizontalSpacer_m() {
    Spacer(modifier = Modifier.width(spacing_m))
}

@Composable
fun HorizontalSpacer_l() {
    Spacer(modifier = Modifier.width(spacing_l))
}