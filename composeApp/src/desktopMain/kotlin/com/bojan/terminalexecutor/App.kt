package com.bojan.terminalexecutor

import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.Dp
import com.bojan.terminalexecutor.settings.TerminalExecutorSettings
import com.bojan.terminalexecutor.ui.screens.MainScreen
import com.bojan.terminalexecutor.viewmodel.MainScreenViewModel
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App(appStateInfo: AppStateInfo, windowsHeight: Dp, settings: TerminalExecutorSettings) {
    var inDarkTheme by remember { mutableStateOf(false) }
    val mainScreenViewModel by remember { mutableStateOf(MainScreenViewModel(appStateInfo = appStateInfo, settings = settings) { inDarkTheme = it }) }
    MaterialTheme(colors = if (inDarkTheme) darkColors() else lightColors()) {
        MainScreen(mainScreenViewModel, windowsHeight)
    }
}