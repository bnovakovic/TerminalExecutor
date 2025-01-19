package com.bojan.terminalexecutor

import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.bojan.terminalexecutor.ui.screens.MainScreen
import com.bojan.terminalexecutor.viewmodel.MainScreenViewModel
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App(appStateInfo: AppStateInfo) {
    var inDarkTheme by remember { mutableStateOf(false) }
    val mainScreenViewModel by remember { mutableStateOf(MainScreenViewModel(appStateInfo = appStateInfo) { inDarkTheme = it }) }
    MaterialTheme(colors = if (inDarkTheme) darkColors() else lightColors()) {
        MainScreen(mainScreenViewModel)
    }
}