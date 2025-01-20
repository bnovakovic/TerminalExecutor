package com.bojan.terminalexecutor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import terminalexecutor.composeapp.generated.resources.Res
import terminalexecutor.composeapp.generated.resources.confirm_exit_changes
import terminalexecutor.composeapp.generated.resources.launcher_icon
import terminalexecutor.composeapp.generated.resources.no
import terminalexecutor.composeapp.generated.resources.yes

fun main() = application {
    val state = rememberWindowState(size = DpSize(1280.dp, 760.dp), position = WindowPosition.Aligned(Alignment.Center))
    val appStateInfo by remember { mutableStateOf(AppStateInfo(changesMade = false)) }
    var showConfirmExit by remember { mutableStateOf(false) }
    Window(
        onCloseRequest = {
            if (appStateInfo.changesMade) {
                showConfirmExit = true
            } else {
                appExit(this)
            }
        },
        title = "TerminalExecutor",
        state = state,
        icon = painterResource(Res.drawable.launcher_icon)
    ) {
        App(appStateInfo)
        if (showConfirmExit) {
            confirmExit(onYes = { this@application.exitApplication() }, onNo = { showConfirmExit = false })
        }
    }
}

private fun appExit(app: ApplicationScope) {
    app.exitApplication()
}

@Composable
private fun confirmExit(onYes: () -> Unit, onNo: () -> Unit) {
    Dialog(onDismissRequest = {}) {
        Column(
            modifier = Modifier.width(300.dp).background(MaterialTheme.colors.surface).padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(stringResource(Res.string.confirm_exit_changes), color = MaterialTheme.colors.onSurface, textAlign = TextAlign.Center, style = MaterialTheme.typography.body1)
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                Spacer(modifier = Modifier.weight(1.0f))
                Button(onClick = onYes) {
                    Text(stringResource(Res.string.yes))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Button(onClick = onNo) {
                    Text(stringResource(Res.string.no))
                }
                Spacer(modifier = Modifier.weight(1.0f))
            }
        }
    }
}