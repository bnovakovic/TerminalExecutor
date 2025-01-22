package com.bojan.terminalexecutor.ui.uistates

import com.bojan.terminalexecutor.enum.ExecuteState
import com.bojan.terminalexecutor.enum.MainScreenDialog
import java.io.File

data class MainScreenUiState(
    val items: ItemsUiState,
    val command: String,
    val allowExecution: Boolean,
    val outputText: String,
    val executeState: ExecuteState,
    val mainScreenDialog: MainScreenDialog,
    val workingDirectory: File,
    val changesMade: Boolean,
    val groupExpanded: Map<String, Boolean>
)