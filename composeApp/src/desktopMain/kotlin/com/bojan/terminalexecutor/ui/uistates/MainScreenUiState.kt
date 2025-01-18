package com.bojan.terminalexecutor.ui.uistates

data class MainScreenUiState(
    val items: List<ListItemGroupUiState>,
    val command: String,
    val allowExecution: Boolean,
    val outputText: String
)