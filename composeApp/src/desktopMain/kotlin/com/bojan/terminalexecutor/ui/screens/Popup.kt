package com.bojan.terminalexecutor.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.bojan.terminalexecutor.enum.MainScreenDialog
import com.bojan.terminalexecutor.settings.APP_PATHS
import com.bojan.terminalexecutor.ui.uistates.MainScreenUiState
import com.bojan.terminalexecutor.viewmodel.MainScreenViewModel
import org.jetbrains.compose.resources.stringResource
import terminalexecutor.composeapp.generated.resources.Res
import terminalexecutor.composeapp.generated.resources.group_delete_text
import terminalexecutor.composeapp.generated.resources.item_delete_text
import terminalexecutor.composeapp.generated.resources.no
import terminalexecutor.composeapp.generated.resources.yes

@Composable
fun MainScreenPopup(
    uiState: MainScreenUiState,
    viewModel: MainScreenViewModel
) {
    when (uiState.mainScreenDialog) {
        is MainScreenDialog.None -> {}

        is MainScreenDialog.AddAnyItem -> {
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

        is MainScreenDialog.AddGroup -> {
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

        is MainScreenDialog.AddAppPath -> {
            Dialog(onDismissRequest = {}) {
                AddAppPathScreen(
                    appMap = viewModel.settings.getMap(APP_PATHS),
                    onCancel = { viewModel.hideDialogue() },
                    onAddItem = { app, path ->
                        viewModel.addAppPath(app, path)
                    },
                )
            }
        }

        is MainScreenDialog.DeleteGroup -> {
            Dialog(onDismissRequest = {}) {
                val group = uiState.mainScreenDialog.groupUiState
                val groupDeleteString = stringResource(Res.string.group_delete_text, group.text)
                YesNoPopupScreen(groupDeleteString, onYes = { viewModel.confirmDeleteGroup(group) }, onNo = { viewModel.hideDialogue() })
            }
        }

        is MainScreenDialog.DeleteItem -> {
            Dialog(onDismissRequest = {}) {
                val parent = uiState.mainScreenDialog.groupUiState
                val index = uiState.mainScreenDialog.itemIndex

                val itemDeleteString = stringResource(Res.string.item_delete_text, parent.items[index].name)
                YesNoPopupScreen(message = itemDeleteString, onYes = { viewModel.confirmDeleteItem(parent, index) }, onNo = { viewModel.hideDialogue() })
            }
        }
    }
}

@Composable
fun YesNoPopupScreen(message: String, onYes: () -> Unit, onNo: () -> Unit) {
    Column(modifier = Modifier.background(MaterialTheme.colors.surface).padding(16.dp).width(400.dp)) {
        Text(message, color = MaterialTheme.colors.onSurface, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(8.dp))
        Row {
            Spacer(modifier = Modifier.weight(1.0f))
            Button(onClick = onYes) {
                Text(stringResource(Res.string.yes))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(onClick = onNo) {
                Text(stringResource(Res.string.no))
            }
            Spacer(modifier = Modifier.weight(1.0f))
        }
    }
}