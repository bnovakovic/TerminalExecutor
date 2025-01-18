package com.bojan.terminalexecutor.ktx

import com.bojan.terminalexecutor.seriazible.ListItemData
import com.bojan.terminalexecutor.ui.uistates.ListItemUiState

fun ListItemData.toListItemUiState() : ListItemUiState = ListItemUiState(name, commands)