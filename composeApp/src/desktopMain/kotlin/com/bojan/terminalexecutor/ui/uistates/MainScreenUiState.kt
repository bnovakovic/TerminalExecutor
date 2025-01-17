package com.bojan.terminalexecutor.ui.uistates

data class MainScreenUiState(
    val items: List<ListItemUiState>,
    val command: String,
    val allowExecution: Boolean,
    val outputText: String
)