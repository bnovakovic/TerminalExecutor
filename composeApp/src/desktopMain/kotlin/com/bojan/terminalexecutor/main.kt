package com.bojan.terminalexecutor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.bojan.terminalexecutor.constants.TE_VERSION
import com.bojan.terminalexecutor.constants.WINDOW_MINIMUM_SIZE
import com.bojan.terminalexecutor.settings.IS_MAXIMIZED
import com.bojan.terminalexecutor.settings.TerminalExecutorSettings
import com.bojan.terminalexecutor.settings.WINDOW_HEIGHT
import com.bojan.terminalexecutor.settings.WINDOW_WIDTH
import com.bojan.terminalexecutor.settings.WINDOW_X
import com.bojan.terminalexecutor.settings.WINDOW_Y
import com.bojan.terminalexecutor.utils.toDp
import com.bojan.terminalexecutor.utils.toInt
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import terminalexecutor.composeapp.generated.resources.Res
import terminalexecutor.composeapp.generated.resources.app_window_title
import terminalexecutor.composeapp.generated.resources.confirm_exit_changes
import terminalexecutor.composeapp.generated.resources.launcher_icon
import terminalexecutor.composeapp.generated.resources.no
import terminalexecutor.composeapp.generated.resources.yes

fun main() = application {
    val settings by remember { mutableStateOf(TerminalExecutorSettings()) }
    settings.loadSettings()
    val windowHeight = settings.getInt(WINDOW_HEIGHT)?.toDp() ?: 760.dp
    val windowWidth = settings.getInt(WINDOW_WIDTH)?.toDp() ?: 1280.dp

    val windowX = settings.getInt(WINDOW_X)
    val windowY = settings.getInt(WINDOW_Y)

    val position = if (windowY != null && windowX != null) {
        WindowPosition.Absolute(windowX.toDp(), windowY.toDp())
    } else {
        WindowPosition.Aligned(Alignment.Center)
    }

    val isMaximized = settings.getBoolean(IS_MAXIMIZED) ?: false
    val placement = if (isMaximized) {
        WindowPlacement.Maximized
    } else {
        WindowPlacement.Floating
    }


    val state = rememberWindowState(size = DpSize(windowWidth, windowHeight), position = position, placement = placement)
    val appStateInfo by remember { mutableStateOf(AppStateInfo(changesMade = false)) }
    var showConfirmExit by remember { mutableStateOf(false) }
    val windowTitle = stringResource(Res.string.app_window_title, TE_VERSION)
    val closeRequest = {
        if (appStateInfo.changesMade) {
            showConfirmExit = true
        } else {
            appExit(this)
        }
    }
    Window(
        onCloseRequest = {
            closeRequest()
        },
        title = windowTitle,
        state = state,
        icon = painterResource(Res.drawable.launcher_icon),
        onKeyEvent = {
            if (it.type == KeyEventType.KeyDown && it.key == Key.Escape) {
                closeRequest()
            }
            return@Window false
        },
    ) {
        window.minimumSize = WINDOW_MINIMUM_SIZE
        saveWindowState(state, settings)
        App(appStateInfo, state.size.height, settings)
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
            Text(
                stringResource(Res.string.confirm_exit_changes),
                color = MaterialTheme.colors.onSurface,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.body1
            )
            VerticalSpacer_m()
            Row(modifier = Modifier.fillMaxWidth()) {
                Spacer(modifier = Modifier.weight(1.0f))
                Button(onClick = onYes) {
                    Text(stringResource(Res.string.yes))
                }
                HorizontalSpacer_m()
                Button(onClick = onNo) {
                    Text(stringResource(Res.string.no))
                }
                Spacer(modifier = Modifier.weight(1.0f))
            }
        }
    }
}

@Composable
private fun saveWindowState(state: WindowState, settings: TerminalExecutorSettings) {
    val isMaximized = state.placement == WindowPlacement.Maximized
    settings.putBoolean(IS_MAXIMIZED, isMaximized)

    if (!isMaximized) {
        settings.putInt(WINDOW_WIDTH, state.size.width.toInt())
        settings.putInt(WINDOW_HEIGHT, state.size.height.toInt())
        settings.putInt(WINDOW_X, state.position.x.toInt())
        settings.putInt(WINDOW_Y, state.position.y.toInt())
    }
}