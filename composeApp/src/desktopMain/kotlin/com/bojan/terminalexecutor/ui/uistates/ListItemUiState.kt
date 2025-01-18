package com.bojan.terminalexecutor.ui.uistates

data class ListItemUiState(
    val name: String,
    val commands: List<String>,
    val isSelected: Boolean
)

data class ListItemGroupUiState(
    val text: String,
    val items: List<ListItemUiState>,
    val children: List<ListItemGroupUiState>
)