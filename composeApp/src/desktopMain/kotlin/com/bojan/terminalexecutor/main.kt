package com.bojan.terminalexecutor

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {

    Window(
        onCloseRequest = ::exitApplication,
        title = "TerminalExecutor",
    ) {
        App()
    }
}