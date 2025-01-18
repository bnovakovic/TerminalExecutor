package com.bojan.terminalexecutor.ktx

import com.bojan.terminalexecutor.seriazible.ListItemGroupData
import com.bojan.terminalexecutor.ui.uistates.ListItemGroupUiState

fun ListItemGroupData.toListItemGroupUiState(): ListItemGroupUiState {
    return ListItemGroupUiState(text, items.map { it.toListItemUiState() }, children.map { it.toListItemGroupUiState() })
}