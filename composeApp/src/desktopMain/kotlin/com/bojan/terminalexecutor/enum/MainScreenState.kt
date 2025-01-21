package com.bojan.terminalexecutor.enum

import com.bojan.terminalexecutor.ui.uistates.ListItemGroupUiState

sealed class MainScreenDialog {
    data object None : MainScreenDialog()

    data object AddAnyItem : MainScreenDialog()

    data object AddGroup : MainScreenDialog()

    data object AddAppPath : MainScreenDialog()

    data class DeleteGroup(val groupUiState: ListItemGroupUiState) : MainScreenDialog()

    data class DeleteItem(val groupUiState: ListItemGroupUiState, val itemIndex: Int) : MainScreenDialog()
}