package com.bojan.terminalexecutor.ktx

import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun Modifier.thinOutline() = this.border(
    width = 1.dp,
    color = MaterialTheme.colors.onSurface,
    shape = RoundedCornerShape(2.dp)
)