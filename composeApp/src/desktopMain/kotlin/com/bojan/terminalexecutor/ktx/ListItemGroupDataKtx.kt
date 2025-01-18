package com.bojan.terminalexecutor.ktx

import com.bojan.terminalexecutor.seriazible.ListItemGroupData
import com.bojan.terminalexecutor.ui.uistates.ListItemGroupUiState
import com.bojan.terminalexecutor.utils.RandomIdGenerator

fun ListItemGroupData.toListItemGroupUiState(idGenerator: RandomIdGenerator): ListItemGroupUiState {
    return ListItemGroupUiState(
        id = idGenerator.generateId(),
        text = text,
        items = items.map { it.toListItemUiState() },
        children = children.map { listItemGroupData ->  listItemGroupData.toListItemGroupUiState(idGenerator) }
    )
}