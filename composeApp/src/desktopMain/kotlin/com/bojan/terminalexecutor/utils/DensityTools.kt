package com.bojan.terminalexecutor.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp

@Composable
fun Dp.toInt(): Int {
    val density = LocalDensity.current
    return with(density) { toPx().toInt() }
}

@Composable
fun Int.toDp(): Dp {
    val density = LocalDensity.current
    return Dp(toFloat() / density.density)
}