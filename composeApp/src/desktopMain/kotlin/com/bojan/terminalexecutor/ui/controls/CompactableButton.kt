package com.bojan.terminalexecutor.ui.controls

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bojan.terminalexecutor.ktx.thinCircleOutlineWithOpacity
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun CompactableButton(text: StringResource, icon: DrawableResource, enabled: Boolean = true, isCompact: Boolean, onClick: () -> Unit) {
    if (!isCompact) {
        Button(onClick = onClick, enabled = enabled) {
            Text(stringResource(text))
        }
    } else {
        IconButton(onClick = onClick, enabled = enabled) {
            Box(modifier = Modifier.thinCircleOutlineWithOpacity().padding(10.dp)) {
                Icon(painterResource(icon), contentDescription = null, tint = MaterialTheme.colors.onSurface)
            }
        }
    }
}