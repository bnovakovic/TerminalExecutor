package com.bojan.terminalexecutor.ui.uistates

data class ListItemGroupUiState(
    val text: String,
    val items: List<ListItemUiState>,
    val children: List<ListItemGroupUiState>
)