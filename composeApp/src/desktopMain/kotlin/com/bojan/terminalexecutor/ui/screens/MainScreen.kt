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
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.bojan.terminalexecutor.constants.JSON_EXTENSION
import com.bojan.terminalexecutor.enum.ExecuteState
import com.bojan.terminalexecutor.enum.MainScreenDialog
import com.bojan.terminalexecutor.ktx.thinOutline
import com.bojan.terminalexecutor.swing.openFileSwingChooser
import com.bojan.terminalexecutor.swing.saveFileSwingChooser
import com.bojan.terminalexecutor.ui.controls.AddRootItem
import com.bojan.terminalexecutor.ui.controls.CommandListGroup
import com.bojan.terminalexecutor.ui.uistates.ListItemGroupUiState
import com.bojan.terminalexecutor.ui.uistates.ListItemUiState
import com.bojan.terminalexecutor.utils.RandomIdGenerator
import com.bojan.terminalexecutor.viewmodel.MainScreenViewModel
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import terminalexecutor.composeapp.generated.resources.Res
import terminalexecutor.composeapp.generated.resources.cancel
import terminalexecutor.composeapp.generated.resources.command
import terminalexecutor.composeapp.generated.resources.command_error_prefix
import terminalexecutor.composeapp.generated.resources.command_failed_prefix
import terminalexecutor.composeapp.generated.resources.copy_icon
import terminalexecutor.composeapp.generated.resources.execute
import terminalexecutor.composeapp.generated.resources.export
import terminalexecutor.composeapp.generated.resources.export_success_message
import terminalexecutor.composeapp.generated.resources.file_already_exist
import terminalexecutor.composeapp.generated.resources.file_not_found_message
import terminalexecutor.composeapp.generated.resources.file_not_found_title
import terminalexecutor.composeapp.generated.resources.group
import terminalexecutor.composeapp.generated.resources.import
import terminalexecutor.composeapp.generated.resources.import_success_message
import terminalexecutor.composeapp.generated.resources.item_type
import terminalexecutor.composeapp.generated.resources.name
import terminalexecutor.composeapp.generated.resources.ok
import terminalexecutor.composeapp.generated.resources.open_file
import terminalexecutor.composeapp.generated.resources.output
import terminalexecutor.composeapp.generated.resources.save_configuration_file
import java.io.File
import javax.swing.filechooser.FileNameExtensionFilter

@Composable
fun MainScreen(viewModel: MainScreenViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val exportTitle = stringResource(Res.string.save_configuration_file)
    val fileExistText = stringResource(Res.string.file_already_exist)
    val fileExistTitle = stringResource(Res.string.file_already_exist)
    val openFileTitle = stringResource(Res.string.open_file)
    val fileNotFoundMessage = stringResource(Res.string.file_not_found_message)
    val fileNotFoundTitle = stringResource(Res.string.file_not_found_title)
    val exportSuccessMessage = stringResource(Res.string.export_success_message)
    val importSuccessMessage = stringResource(Res.string.import_success_message)
    val commandFailPrefix = stringResource(Res.string.command_failed_prefix)
    val commandErrorPrefix = stringResource(Res.string.command_error_prefix)
    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colors.surface).padding(8.dp)) {
        ItemList(
            items = uiState.items.items,
            modifier = Modifier.weight(0.5f),
            onAddItem = { viewModel.showAddItemDialogue(it) },
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
                    title = exportTitle,
                    currentDir = File(""),
                    initialFileName = "TerminalExecutorConfiguration.$JSON_EXTENSION",
                    onFileConfirm = { viewModel.export(it, exportSuccessMessage) },
                    fileNameExtensionFilter = FileNameExtensionFilter("JSON File (.$JSON_EXTENSION)", JSON_EXTENSION),
                    overwriteMessage = fileExistText,
                    overwriteTitle = fileExistTitle
                )
            },
            onImport = {
                openFileSwingChooser(
                    title = openFileTitle,
                    currentDir = File(""),
                    initialFileName = "TerminalExecutorConfiguration.$JSON_EXTENSION",
                    onFileConfirm = { viewModel.import(it, importSuccessMessage) },
                    fileNameExtensionFilter = FileNameExtensionFilter("JSON File (.$JSON_EXTENSION)", JSON_EXTENSION),
                    fileDoesNotExistTitle = fileNotFoundTitle,
                    fileDoesNotExistMessage = fileNotFoundMessage
                )
            }
        ) {
            viewModel.execute(
                commandFailPrefix,
                commandErrorPrefix,
            )
        }
    }
    when (uiState.mainScreenDialog) {
        MainScreenDialog.NONE -> {}
        MainScreenDialog.ADD_ITEM -> {
            Dialog(onDismissRequest = {}) {
                AddItemScreen(
                    randomIdGenerator = viewModel.idGenerator,
                    onCancel = { viewModel.hideDialogue() },
                    onAddItem = { viewModel.addItem(it) },
                    onAddGroup = { viewModel.addGroup(it) }
                )
            }
        }
    }
}

@Composable
fun AddItemScreen(
    modifier: Modifier = Modifier,
    randomIdGenerator: RandomIdGenerator,
    onCancel: () -> Unit,
    onAddItem: (ListItemUiState) -> Unit,
    onAddGroup: (ListItemGroupUiState) -> Unit
) {
    Column(modifier = Modifier.background(MaterialTheme.colors.surface).padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        val radioOptions = listOf(stringResource(Res.string.group), stringResource(Res.string.command))
        val (selectedOption, onOptionSelected) = remember { mutableStateOf(radioOptions[0]) }
        val addingCommand = radioOptions.indexOf(selectedOption) == 1
        Text(stringResource(Res.string.item_type))
        Spacer(modifier = Modifier.height(4.dp))
        Row(modifier.selectableGroup().thinOutline()) {
            radioOptions.forEach { text ->
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
            label = { Text(stringResource(Res.string.name)) },
            singleLine = true,

            )

        Spacer(modifier.height(8.dp))

        var commandText by remember { mutableStateOf("") }
        if (addingCommand) {
            TextField(
                value = commandText,
                onValueChange = { commandText = it },
                modifier = Modifier.width(800.dp).thinOutline(),
                readOnly = false,
                label = { Text(stringResource(Res.string.command)) },
            )

            Spacer(modifier.height(8.dp))
        }

        Row {
            Spacer(modifier = Modifier.weight(1.0f))
            Button(onClick = onCancel) {
                Text(stringResource(Res.string.cancel))
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    if (addingCommand) {
                        onAddItem(ListItemUiState(nameText, commandText.split(" ")))
                    } else {
                        onAddGroup(ListItemGroupUiState(randomIdGenerator.generateId(), nameText, emptyList(), emptyList()))
                    }
                }
            ) {
                Text(stringResource(Res.string.ok))
            }
        }
    }
}

@Composable
fun ItemList(items: List<ListItemGroupUiState>, modifier: Modifier, onAddItem: (String) -> Unit, onSelected: (List<String>) -> Unit) {
    val listState = rememberLazyListState()
    Row(modifier = Modifier.fillMaxWidth().thinOutline().then(modifier)) {
        if (items.isNotEmpty()) {
            LazyColumn(state = listState, modifier = Modifier.weight(1.0f)) {
                itemsIndexed(items) { index, item ->
                    item.apply {
                        CommandListGroup(
                            id = item.id,
                            text = text,
                            items = this.items,
                            children = children,
                            modifier = Modifier,
                            onAddItem = {
                                onAddItem(it)
                            },
                            onItemSelected = onSelected
                        )
                    }
                    if (index == items.lastIndex) {
                        AddRootItem { onAddItem("") }
                    }
                }
            }
            VerticalScrollbar(
                adapter = rememberScrollbarAdapter(scrollState = listState),
                modifier = Modifier.width(14.dp).padding(horizontal = 2.dp, vertical = 1.dp)
            )
        } else {
            AddRootItem { onAddItem("") }
        }
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
            label = { Text(stringResource(Res.string.command)) },
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
            label = { Text(stringResource(Res.string.output)) },
            trailingIcon = {
                IconButton(onClick = { clipboardManager.setText(buildAnnotatedString { append(output) }) }) {
                    Icon(painter = painterResource(Res.drawable.copy_icon), contentDescription = null)
                }
            }
        )
        Spacer(modifier = Modifier.height(10.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Button(onClick = { onExecute() }, enabled = allowExecution) {
                Text(stringResource(Res.string.execute))
            }
            Spacer(modifier = Modifier.width(8.dp))
            when (executeState) {
                ExecuteState.NONE -> {}
                ExecuteState.WORKING -> {
                    CircularProgressIndicator(modifier = Modifier.width(32.dp).padding(0.dp), color = MaterialTheme.colors.secondary)
                }

                ExecuteState.ERROR -> {
                    Icon(
                        Icons.Default.Warning,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp),
                        tint = MaterialTheme.colors.error
                    )
                }

                ExecuteState.OK -> {
                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(32.dp), MaterialTheme.colors.primary)
                }
            }
            Spacer(modifier = Modifier.weight(1.0f))
            Button(onClick = onImport) {
                Text(stringResource(Res.string.import))
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = onExport) {
                Text(stringResource(Res.string.export))
            }

        }
    }
}