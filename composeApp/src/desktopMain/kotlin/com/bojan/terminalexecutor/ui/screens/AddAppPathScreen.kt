package com.bojan.terminalexecutor.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.bojan.terminalexecutor.HorizontalSpacer_s
import com.bojan.terminalexecutor.VerticalSpacer_l
import com.bojan.terminalexecutor.VerticalSpacer_s
import com.bojan.terminalexecutor.ktx.thinOutline
import org.jetbrains.compose.resources.stringResource
import terminalexecutor.composeapp.generated.resources.Res
import terminalexecutor.composeapp.generated.resources.add_app_description
import terminalexecutor.composeapp.generated.resources.app
import terminalexecutor.composeapp.generated.resources.cancel
import terminalexecutor.composeapp.generated.resources.ok
import terminalexecutor.composeapp.generated.resources.path

@Composable
fun AddAppPathScreen(
    modifier: Modifier = Modifier,
    appMap: Map<String, String>,
    onCancel: () -> Unit,
    onAddItem: (String, String) -> Unit
) {
    Column(
        modifier = Modifier.run { background(MaterialTheme.colors.surface).padding(16.dp) },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        var appText by remember { mutableStateOf("") }
        var pathText by remember { mutableStateOf("") }

        val confirmEnabled = appText.trim().isNotEmpty() && pathText.trim().isNotEmpty()
        val focusRequester = remember { FocusRequester() }

        Text(stringResource(Res.string.add_app_description), color = MaterialTheme.colors.onSurface, textAlign = TextAlign.Center)
        VerticalSpacer_l()


        TextField(
            value = appText,
            onValueChange = {
                appText = it
                appMap[appText]?.let { found ->
                    pathText = found
                }
            },
            modifier = Modifier.width(800.dp).thinOutline().focusRequester(focusRequester).onKeyEvent { keyEvent ->
                if (keyEvent.key == Key.Escape) {
                    onCancel()
                    true
                } else {
                    false
                }
            },
            readOnly = false,
            label = { Text(stringResource(Res.string.app)) },
            singleLine = true,
            colors = TextFieldDefaults.textFieldColors(textColor = MaterialTheme.colors.onSurface),
        )
        VerticalSpacer_s()
        TextField(
            value = pathText,
            onValueChange = { pathText = it },
            modifier = Modifier.width(800.dp).thinOutline().onKeyEvent { keyEvent ->
                if (keyEvent.key == Key.Escape) {
                    onCancel()
                    true
                } else {
                    false
                }
            },
            readOnly = false,
            label = { Text(stringResource(Res.string.path)) },
            colors = TextFieldDefaults.textFieldColors(textColor = MaterialTheme.colors.onSurface),
        )

        VerticalSpacer_s()

        Row {
            Spacer(modifier = Modifier.weight(1.0f))
            Button(onClick = onCancel) {
                Text(stringResource(Res.string.cancel))
            }
            HorizontalSpacer_s()
            Button(
                onClick = {
                    onAddItem(appText, pathText)
                },
                enabled = confirmEnabled
            ) {
                Text(stringResource(Res.string.ok))
            }
        }

        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }
    }
}