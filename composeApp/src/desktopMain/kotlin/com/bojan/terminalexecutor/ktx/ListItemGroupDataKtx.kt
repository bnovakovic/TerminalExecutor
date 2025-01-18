package com.bojan.terminalexecutor.ktx

import com.bojan.terminalexecutor.seriazible.ListItemGroupData
import com.bojan.terminalexecutor.ui.uistates.ListItemGroupUiState

fun ListItemGroupData.toListItemGroupUiState(id: String): ListItemGroupUiState {
    return ListItemGroupUiState(
        id = id,
        text = text,
        items = items.map { it.toListItemUiState() },
        children = children.mapIndexed { index, listItemGroupData ->  listItemGroupData.toListItemGroupUiState("$id,$index") }
    )
}