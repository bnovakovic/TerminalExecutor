package com.bojan.terminalexecutor.ktx

import com.bojan.terminalexecutor.seriazible.ListItemGroupData
import com.bojan.terminalexecutor.ui.uistates.ListItemGroupUiState

fun ListItemGroupUiState.toListItemGroupData(): ListItemGroupData {
    return ListItemGroupData(text, items.map { it.toListItemData() }, children.map { it.toListItemGroupData() })
}