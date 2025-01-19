package com.bojan.terminalexecutor.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Dialog
import com.bojan.terminalexecutor.enum.MainScreenDialog
import com.bojan.terminalexecutor.ui.uistates.MainScreenUiState
import com.bojan.terminalexecutor.viewmodel.MainScreenViewModel

@Composable
fun MainScreenPopup(
    uiState: MainScreenUiState,
    viewModel: MainScreenViewModel
) {
    when (uiState.mainScreenDialog) {
        MainScreenDialog.NONE -> {}
        MainScreenDialog.ADD_ANY_ITEM -> {
            Dialog(onDismissRequest = {}) {
                AddItemScreen(
                    randomIdGenerator = viewModel.idGenerator,
                    groupOnly = false,
                    onCancel = { viewModel.hideDialogue() },
                    onAddItem = { viewModel.addItem(it) },
                    onAddGroup = { viewModel.addGroup(it) }
                )
            }
        }

        MainScreenDialog.ADD_GROUP -> {
            Dialog(onDismissRequest = {}) {
                AddItemScreen(
                    randomIdGenerator = viewModel.idGenerator,
                    groupOnly = true,
                    onCancel = { viewModel.hideDialogue() },
                    onAddItem = { viewModel.addItem(it) },
                    onAddGroup = { viewModel.addGroup(it) }
                )
            }
        }
    }
}