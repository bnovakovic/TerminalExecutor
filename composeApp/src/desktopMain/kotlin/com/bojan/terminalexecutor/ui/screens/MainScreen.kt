package com.bojan.terminalexecutor.ui.screens

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.Role.Companion.RadioButton
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.Popup
import com.bojan.terminalexecutor.constants.JSON_EXTENSION
import com.bojan.terminalexecutor.enum.ExecuteState
import com.bojan.terminalexecutor.enum.MainScreenDialog
import com.bojan.terminalexecutor.ktx.thinOutline
import com.bojan.terminalexecutor.swing.openFileSwingChooser
import com.bojan.terminalexecutor.swing.saveFileSwingChooser
import com.bojan.terminalexecutor.ui.controls.CommandListGroup
import com.bojan.terminalexecutor.ui.uistates.ListItemGroupUiState
import com.bojan.terminalexecutor.viewmodel.MainScreenViewModel
import org.jetbrains.compose.resources.painterResource
import terminalexecutor.composeapp.generated.resources.Res
import terminalexecutor.composeapp.generated.resources.copy_icon
import java.io.File
import javax.swing.filechooser.FileNameExtensionFilter

@Composable
fun MainScreen(viewModel: MainScreenViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colors.surface).padding(8.dp)) {
        ItemList(
            items = uiState.items,
            modifier = Modifier.weight(0.5f),
            onAddItem = { viewModel.showAddItemDialogue() },
            onSelected = { viewModel.itemSelected(it) }
        )
        Spacer(modifier = Modifier.height(8.dp))
        ActionItems(
            modifier = Modifier.weight(0.5f),
            command = uiState.command,
            output = uiState.outputText,
            allowExecution = uiState.allowExecution,
            executeState = uiState.executeState,
            onExport = {
                saveFileSwingChooser(
                    title = "Save configuration file",
                    currentDir = File(""),
                    initialFileName = "TerminalExecutorConfiguration.$JSON_EXTENSION",
                    onFileConfirm = { viewModel.export(it) },
                    fileNameExtensionFilter = FileNameExtensionFilter("JSON File (.$JSON_EXTENSION)", JSON_EXTENSION),
                    overwriteMessage = "File already exists. Overwrite?",
                    overwriteTitle = "Overwrite file?"
                )
            },
            onImport = { openFileSwingChooser(
                title = "Open file swing chooser",
                currentDir = File(""),
                initialFileName = "TerminalExecutorConfiguration.$JSON_EXTENSION",
                onFileConfirm = { viewModel.import(it) },
                fileNameExtensionFilter = FileNameExtensionFilter("JSON File (.$JSON_EXTENSION)", JSON_EXTENSION),
                fileDoesNotExistTitle = "File not found",
                fileDoesNotExistMessage = "File not found. Check file name and try again"
            ) }
        ) { viewModel.execute() }
    }
    when (uiState.mainScreenDialog) {
        MainScreenDialog.NONE -> {}
        MainScreenDialog.ADD_ITEM -> { Dialog(onDismissRequest = {}) { AddItemScreen(onCancel = { viewModel.hideDialogue() }, onConfirm = {}) } }
    }
}

@Composable
fun AddItemScreen(modifier: Modifier = Modifier, onCancel: () -> Unit, onConfirm: () -> Unit) {
    Column(modifier = Modifier.background(MaterialTheme.colors.surface).padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        val radioOptions = listOf("Command", "Group")
        val (selectedOption, onOptionSelected) = remember { mutableStateOf(radioOptions[0]) }
        Text("Item type")
        Spacer(modifier = Modifier.height(4.dp))
        Row (modifier.selectableGroup().thinOutline()) {
            radioOptions.forEachIndexed  { index, text ->
                Row(
                    Modifier
                        .height(56.dp)
                        .width(160.dp)
                        .selectable(
                            selected = (text == selectedOption),
                            onClick = { onOptionSelected(text) },
                            role = Role.RadioButton
                        )
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (text == selectedOption),
                        onClick = null
                    )
                    Text(
                        text = text,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
            }
        }

        Spacer(modifier.height(8.dp))

        var nameText by remember { mutableStateOf("") }
        TextField(
            value = nameText,
            onValueChange = { nameText = it },
            modifier = Modifier.width(800.dp).thinOutline(),
            readOnly = false,
            label = { Text("Name") },
            singleLine = true,

        )

        Spacer(modifier.height(8.dp))

        if (radioOptions.indexOf(selectedOption) == 0) {
            var commandText by remember { mutableStateOf("") }
            TextField(
                value = commandText,
                onValueChange = { commandText = it },
                modifier = Modifier.width(800.dp).thinOutline(),
                readOnly = false,
                label = { Text("Command") },
            )

            Spacer(modifier.height(8.dp))
        }

        Row {
            Spacer(modifier = Modifier.weight(1.0f))
            Button(onClick = onCancel) {
                Text("Cancel")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = onConfirm) {
                Text("OK")
            }
        }
    }
}

@Composable
fun ItemList(items: List<ListItemGroupUiState>, modifier: Modifier, onAddItem: () -> Unit, onSelected: (List<String>) -> Unit) {
    val listState = rememberLazyListState()
    Row(modifier = Modifier.fillMaxWidth().thinOutline().then(modifier)) {
        LazyColumn(state = listState, modifier = Modifier.weight(1.0f)) {
            items(items) { item ->
                item.apply {
                    CommandListGroup(text, this.items, children, Modifier, onAddItem, onSelected)
                }

            }
        }
        VerticalScrollbar(adapter = rememberScrollbarAdapter(scrollState = listState), modifier = Modifier.width(14.dp).padding(horizontal = 2.dp, vertical = 1.dp))
    }
}

@Composable
fun ActionItems(
    modifier: Modifier,
    command: String,
    output: String,
    allowExecution: Boolean,
    executeState: ExecuteState,
    onExport: () -> Unit,
    onImport: () -> Unit,
    onExecute: () -> Unit,
) {
    val clipboardManager = LocalClipboardManager.current
    Column(modifier = Modifier.fillMaxWidth().then(modifier)) {
        TextField(
            value = command,
            onValueChange = {},
            modifier = Modifier.height(100.dp).fillMaxWidth().thinOutline(),
            readOnly = true,
            label = { Text("Command") },
            trailingIcon = {
                IconButton(onClick = { clipboardManager.setText(buildAnnotatedString { append(command) }) }) {
                    Icon(painter = painterResource(Res.drawable.copy_icon), contentDescription = null)
                }
            }
        )
        Spacer(modifier = Modifier.height(10.dp))
        TextField(
            value = output,
            onValueChange = {},
            modifier = Modifier.weight(1.0f).fillMaxWidth().thinOutline(),
            readOnly = true,
            label = { Text("Output") },
            trailingIcon = {
                IconButton(onClick = { clipboardManager.setText(buildAnnotatedString { append(output) }) }) {
                    Icon(painter = painterResource(Res.drawable.copy_icon), contentDescription = null)
                }
            }
        )
        Spacer(modifier = Modifier.height(10.dp))
        Row (verticalAlignment = Alignment.CenterVertically) {
            Button(onClick = { onExecute() }, enabled = allowExecution) {
                Text("Execute")
            }
            Spacer(modifier = Modifier.width(8.dp))
            when (executeState) {
                ExecuteState.NONE -> { }
                ExecuteState.WORKING -> { CircularProgressIndicator(modifier = Modifier.width(32.dp).padding(0.dp), color = MaterialTheme.colors.secondary) }
                ExecuteState.ERROR -> { Icon(Icons.Default.Warning, contentDescription = null, modifier = Modifier.size(32.dp), tint = MaterialTheme.colors.error) }
                ExecuteState.OK -> { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(32.dp), MaterialTheme.colors.primary) }
            }
            Spacer(modifier = Modifier.weight(1.0f))
            Button(onClick = onImport) {
                Text("Import")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = onExport) {
                Text("Export")
            }

        }
    }
}