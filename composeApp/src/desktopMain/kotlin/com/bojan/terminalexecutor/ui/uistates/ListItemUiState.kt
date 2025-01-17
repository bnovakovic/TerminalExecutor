package com.bojan.terminalexecutor.ui.uistates

data class ListItemUiState(
    val name: String,
    val commands: List<String>,
    val isFavorite: Boolean,
    val isSelected: Boolean
)