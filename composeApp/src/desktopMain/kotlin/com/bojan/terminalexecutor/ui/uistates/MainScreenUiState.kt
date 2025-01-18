package com.bojan.terminalexecutor.ui.uistates

import com.bojan.terminalexecutor.enum.ExecuteState
import com.bojan.terminalexecutor.enum.MainScreenDialog

data class MainScreenUiState(
    val items: List<ListItemGroupUiState>,
    val command: String,
    val allowExecution: Boolean,
    val outputText: String,
    val executeState: ExecuteState,
    val mainScreenDialog: MainScreenDialog
)