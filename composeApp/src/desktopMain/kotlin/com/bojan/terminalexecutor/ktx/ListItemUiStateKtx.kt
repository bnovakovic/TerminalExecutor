package com.bojan.terminalexecutor.ktx

import com.bojan.terminalexecutor.seriazible.ListItemData
import com.bojan.terminalexecutor.seriazible.ParamInfo
import com.bojan.terminalexecutor.ui.uistates.ListItemUiState

fun ListItemUiState.toListItemData() : ListItemData {
    return ListItemData(name, commands, params.map { ParamInfo(it.name, it.value) })
}