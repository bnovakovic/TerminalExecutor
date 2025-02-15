package com.bojan.terminalexecutor.ui.controls

import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bojan.terminalexecutor.HorizontalSpacer_xs
import org.jetbrains.compose.resources.stringResource
import terminalexecutor.composeapp.generated.resources.Res
import terminalexecutor.composeapp.generated.resources.device
import terminalexecutor.composeapp.generated.resources.none

@Composable
fun DeviceSelector(devices: List<String>, selectedDevice: Int, onDeviceSelected: (Int) -> Unit) {
    HorizontalSpacer_xs()
    Text(stringResource(Res.string.device), color = MaterialTheme.colors.onSurface)
    HorizontalSpacer_xs()
    DropDownMenu(
        selectedDevice,
        items = listOf(stringResource(Res.string.none)) + devices,
        onItemSelected = {
            onDeviceSelected(it)
        },
        modifier = Modifier.size(150.dp, 30.dp)
    )
}
