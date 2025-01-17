package com.bojan.terminalexecutor

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import com.bojan.terminalexecutor.ui.screens.MainScreen
import com.bojan.terminalexecutor.viewmodel.MainScreenViewModel
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    MaterialTheme {
        MainScreen(MainScreenViewModel())
    }
}