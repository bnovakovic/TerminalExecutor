package com.bojan.terminalexecutor.ui.uistates

data class ListItemGroupUiState(
    val id: String,
    val text: String,
    val items: List<ListItemUiState>,
    val children: List<ListItemGroupUiState>
)