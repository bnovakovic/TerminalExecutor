package com.bojan.terminalexecutor.ktx

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TextFieldDefaults.UnfocusedIndicatorLineOpacity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.unit.dp
import com.bojan.terminalexecutor.constants.DOUBLE_CLICK_DELAY

@Composable
fun Modifier.thinOutline() = this.border(
    width = 1.dp,
    color = MaterialTheme.colors.onSurface,
    shape = RoundedCornerShape(2.dp)
)

@Composable
fun Modifier.thinCircleOutlineWithOpacity() = this.border(
    width = 2.dp,
    color = MaterialTheme.colors.onSurface.copy(alpha = UnfocusedIndicatorLineOpacity),
    shape = CircleShape
)

@Composable
fun Modifier.doubleClickable(onClick: () -> Unit, onDoubleClick: () -> Unit): Modifier = composed {
    var lastClickTime by remember { mutableStateOf(0L) }
    clickable {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime < DOUBLE_CLICK_DELAY) {
            onDoubleClick()
        } else {
            onClick()
        }
        lastClickTime = currentTime
    }
}