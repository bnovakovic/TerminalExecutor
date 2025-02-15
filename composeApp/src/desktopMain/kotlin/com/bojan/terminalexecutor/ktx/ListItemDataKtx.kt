package com.bojan.terminalexecutor.ktx

import com.bojan.terminalexecutor.seriazible.ListItemData
import com.bojan.terminalexecutor.ui.uistates.ListItemUiState
import com.bojan.terminalexecutor.ui.uistates.ParamInfoUiState

fun ListItemData.toListItemUiState() : ListItemUiState = ListItemUiState(name, commands, params.map { ParamInfoUiState(it.name, it.value) })