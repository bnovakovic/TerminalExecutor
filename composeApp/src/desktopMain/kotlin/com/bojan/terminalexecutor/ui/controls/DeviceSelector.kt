package com.bojan.terminalexecutor.ui.controls

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import terminalexecutor.composeapp.generated.resources.Res
import terminalexecutor.composeapp.generated.resources.device
import terminalexecutor.composeapp.generated.resources.none

@Composable
fun DeviceSelector(devices: List<String>, selectedDevice: Int, onDeviceSelected: (Int) -> Unit) {
    Spacer(modifier = Modifier.width(4.dp))
    Text(stringResource(Res.string.device), color = MaterialTheme.colors.onSurface)
    Spacer(modifier = Modifier.width(4.dp))
    DropDownMenu(
        selectedDevice,
        items = listOf(stringResource(Res.string.none)) + devices,
        onItemSelected = {
            onDeviceSelected(it)
        },
        modifier = Modifier.size(150.dp, 30.dp)
    )
}
