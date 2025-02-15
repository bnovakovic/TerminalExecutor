package com.bojan.terminalexecutor.ui.screens

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.bojan.terminalexecutor.DraggableVerticalSpacer
import com.bojan.terminalexecutor.HorizontalSpacer_s
import com.bojan.terminalexecutor.HorizontalSpacer_xs
import com.bojan.terminalexecutor.constants.COMPACT_MODE_BUTTON_WIDTH
import com.bojan.terminalexecutor.constants.COMPACT_MODE_TOP_BAR_WIDTH
import com.bojan.terminalexecutor.constants.JSON_EXTENSION
import com.bojan.terminalexecutor.enum.ExecuteState
import com.bojan.terminalexecutor.ktx.thinCircleOutlineWithOpacity
import com.bojan.terminalexecutor.ktx.thinOutline
import com.bojan.terminalexecutor.settings.EXPORT_PATH
import com.bojan.terminalexecutor.settings.IMPORT_PATH
import com.bojan.terminalexecutor.settings.INPUT_FIELDS_SCREEN_OFFSET
import com.bojan.terminalexecutor.settings.IS_IN_DARK_MODE
import com.bojan.terminalexecutor.settings.MAIN_SCREEN_OFFSET
import com.bojan.terminalexecutor.settings.WORKING_DIR
import com.bojan.terminalexecutor.spacing_s
import com.bojan.terminalexecutor.swing.folderSwingChooser
import com.bojan.terminalexecutor.swing.openFileSwingChooser
import com.bojan.terminalexecutor.swing.saveFileSwingChooser
import com.bojan.terminalexecutor.ui.controls.AddRootItem
import com.bojan.terminalexecutor.ui.controls.CommandListGroup
import com.bojan.terminalexecutor.ui.controls.CompactableButton
import com.bojan.terminalexecutor.ui.controls.DeviceSelector
import com.bojan.terminalexecutor.ui.controls.ParamsList
import com.bojan.terminalexecutor.ui.uistates.ListItemGroupUiState
import com.bojan.terminalexecutor.ui.uistates.ParamInfoUiState
import com.bojan.terminalexecutor.utils.toDp
import com.bojan.terminalexecutor.utils.toInt
import com.bojan.terminalexecutor.viewmodel.MainScreenViewModel
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import terminalexecutor.composeapp.generated.resources.Res
import terminalexecutor.composeapp.generated.resources.add
import terminalexecutor.composeapp.generated.resources.add_app_path
import terminalexecutor.composeapp.generated.resources.command
import terminalexecutor.composeapp.generated.resources.copy_icon
import terminalexecutor.composeapp.generated.resources.dark_mode
import terminalexecutor.composeapp.generated.resources.execute
import terminalexecutor.composeapp.generated.resources.export
import terminalexecutor.composeapp.generated.resources.file_already_exist
import terminalexecutor.composeapp.generated.resources.file_import
import terminalexecutor.composeapp.generated.resources.file_not_found_message
import terminalexecutor.composeapp.generated.resources.file_not_found_title
import terminalexecutor.composeapp.generated.resources.folder_open
import terminalexecutor.composeapp.generated.resources.import
import terminalexecutor.composeapp.generated.resources.open_file
import terminalexecutor.composeapp.generated.resources.output
import terminalexecutor.composeapp.generated.resources.params_text
import terminalexecutor.composeapp.generated.resources.save
import terminalexecutor.composeapp.generated.resources.save_as
import terminalexecutor.composeapp.generated.resources.save_configuration_file
import terminalexecutor.composeapp.generated.resources.select_working_dir
import terminalexecutor.composeapp.generated.resources.terminal
import terminalexecutor.composeapp.generated.resources.working_directory
import java.io.File
import javax.swing.filechooser.FileNameExtensionFilter

@Composable
fun MainScreen(viewModel: MainScreenViewModel, windowHeight: Dp) {
    val uiState by viewModel.uiState.collectAsState()
    val selectWorkingDir = stringResource(Res.string.select_working_dir)
    val loadedOffset = viewModel.settings.getInt(MAIN_SCREEN_OFFSET) ?: 0
    var offset by remember { mutableIntStateOf(loadedOffset) }
    viewModel.settings.putInt(MAIN_SCREEN_OFFSET, offset)
    val intDefaultHeight = windowHeight.toInt()
    var availableHeight by remember { mutableIntStateOf(intDefaultHeight) }

    val defaultItemSpacing = spacing_s

    val actionButtonsHeight = 50.dp.toInt()
    val topBarHeight = 60.dp.toInt()
    val remainingHeight = availableHeight - topBarHeight - actionButtonsHeight
    val itemListHeight = (remainingHeight / 2) - defaultItemSpacing.toInt() + offset
    val infoFieldsHeight = (remainingHeight / 2) - defaultItemSpacing.toInt() - offset

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.surface)
            .padding(defaultItemSpacing)
            .onSizeChanged { size ->
                availableHeight = size.height
            }
    ) {
        TopBar(
            modifier = Modifier.height(topBarHeight.toDp()),
            deviceListVisible = uiState.isAdbCommand,
            devices = uiState.adbDevices,
            selectedDevice = uiState.selectedDevice,
            currentDir = uiState.workingDirectory,
            onDeviceSelected = { viewModel.onAdbDeviceSelected(it) },
            defaultSwitchValue = viewModel.settings.getBoolean(IS_IN_DARK_MODE) ?: false,
            onDarkModeEnabled = { viewModel.changeTheme(isDark = it) },
            onChangeWorkingDir = {
                val previousWorkingDir = viewModel.settings.getString(WORKING_DIR) ?: ""
                folderSwingChooser(title = selectWorkingDir, currentDir = File(previousWorkingDir)) {
                    viewModel.workingDirChange(it)
                }
            }
        )
        Spacer(modifier = Modifier.height(defaultItemSpacing))
        ItemList(
            items = uiState.items.items,
            modifier = Modifier.height(itemListHeight.toDp()),
            viewModel = viewModel,
            expandedMap = uiState.groupExpanded
        )
        DraggableVerticalSpacer(size = defaultItemSpacing, defaultOffset = loadedOffset.toFloat(), onDragOffset = { offset = it.toInt() })
        InfoFields(
            height = infoFieldsHeight.toDp(),
            command = uiState.command,
            output = uiState.outputText,
            params = uiState.paramsList,
            viewModel = viewModel,
            itemSpacing = defaultItemSpacing
        )
        Spacer(modifier = Modifier.height(defaultItemSpacing))
        ActionButtons(
            modifier = Modifier.height(actionButtonsHeight.toDp()),
            allowExecution = uiState.allowExecution,
            executeState = uiState.executeState,
            hasChanges = uiState.changesMade,
            viewModel = viewModel
        )
    }
    MainScreenPopup(uiState, viewModel)
}

@Composable
private fun TopBar(
    modifier: Modifier,
    deviceListVisible: Boolean,
    devices: List<String>,
    selectedDevice: Int,
    currentDir: File,
    onDeviceSelected: (Int) -> Unit,
    defaultSwitchValue: Boolean,
    onDarkModeEnabled: (Boolean) -> Unit,
    onChangeWorkingDir: () -> Unit
) {

    BoxWithConstraints {
        val compactMode = deviceListVisible && maxWidth < COMPACT_MODE_TOP_BAR_WIDTH
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().then(modifier)
        ) {


            if (compactMode) {
                IconButton(onClick = onChangeWorkingDir, modifier = Modifier.thinCircleOutlineWithOpacity()) {
                    Icon(painterResource(Res.drawable.folder_open), contentDescription = null, tint = MaterialTheme.colors.onSurface)
                }
                Spacer(modifier = Modifier.weight(1.0f))
            } else {
                TextField(
                    value = currentDir.toString(),
                    onValueChange = {},
                    modifier = Modifier.weight(1.0f).thinOutline(),
                    readOnly = true,
                    singleLine = true,
                    label = { Text(stringResource(Res.string.working_directory)) },
                    trailingIcon = {
                        IconButton(onClick = onChangeWorkingDir) {
                            Icon(Icons.Default.Edit, contentDescription = null)
                        }
                    },
                    colors = TextFieldDefaults.textFieldColors(textColor = MaterialTheme.colors.onSurface)
                )
            }
            if (deviceListVisible) {
                DeviceSelector(devices, selectedDevice, onDeviceSelected)
            }
            HorizontalSpacer_xs()
            var checked by remember { mutableStateOf(defaultSwitchValue) }
            Text(stringResource(Res.string.dark_mode), color = MaterialTheme.colors.onSurface)
            HorizontalSpacer_xs()
            Switch(
                checked = checked,
                onCheckedChange = {
                    checked = it
                    onDarkModeEnabled(checked)
                },
            )
        }
    }
}

@Composable
fun ItemList(
    items: List<ListItemGroupUiState>,
    modifier: Modifier,
    viewModel: MainScreenViewModel,
    expandedMap: Map<String, Boolean>
) {
    val listState = rememberLazyListState()
    val defaultValue = items.size == 1
    Row(modifier = Modifier.fillMaxWidth().thinOutline().then(modifier)) {
        if (items.isNotEmpty()) {
            LazyColumn(state = listState, modifier = Modifier.weight(1.0f)) {
                itemsIndexed(items) { index, item ->
                    val expanded = expandedMap[item.id] ?: defaultValue
                    item.apply {
                        CommandListGroup(
                            groupUiState = item,
                            modifier = Modifier,
                            expanded = expanded,
                            onExpand = { groupId, isExpanded -> viewModel.expandCollapseGroup(groupId, isExpanded) },
                            onAddItem = {
                                viewModel.showAddItemDialogue(it)
                            },
                            onItemSelected = { commands, params ->
                                viewModel.changeCommand(commands = commands, params = params)
                            },
                            onDeleteGroup = { viewModel.askDeleteGroup(it) },
                            onDeleteItem = { parent, index ->
                                viewModel.askDeleteItem(parent, index)
                            },
                            expandedMap = expandedMap
                        )
                    }
                    if (index == items.lastIndex) {
                        AddRootItem(stringResource(Res.string.add)) { viewModel.showAddItemDialogue("") }
                    }
                }
            }
            VerticalScrollbar(
                adapter = rememberScrollbarAdapter(scrollState = listState),
                modifier = Modifier.width(14.dp).padding(horizontal = 2.dp, vertical = 1.dp)
            )
        } else {
            AddRootItem(stringResource(Res.string.add)) { viewModel.showAddItemDialogue("") }
        }
    }
}

@Composable
fun InfoFields(
    height: Dp,
    command: String,
    output: String,
    params: List<ParamInfoUiState>,
    viewModel: MainScreenViewModel,
    itemSpacing: Dp
) {
    val loadedOffset = viewModel.settings.getInt(INPUT_FIELDS_SCREEN_OFFSET) ?: 0
    var offset by remember { mutableIntStateOf(loadedOffset) }
    viewModel.settings.putInt(INPUT_FIELDS_SCREEN_OFFSET, offset)

    val availableHeight = height.toInt()
    val paramsHeight = 55.dp.toInt()

    val remainingHeight = availableHeight - paramsHeight - itemSpacing.toInt()
    val commandHeight = (remainingHeight / 2) + offset - (itemSpacing.toInt() * 2)
    val outputHeight = (remainingHeight / 2) - offset - itemSpacing.toInt()

    val clipboardManager = LocalClipboardManager.current
    Column(modifier = Modifier.fillMaxWidth().height(height)) {
        var paramsText by remember { mutableStateOf("") }
        TextField(
            value = paramsText,
            onValueChange = {
                paramsText = it
                viewModel.paramsTextUpdated(it)
            },
            modifier = Modifier.fillMaxWidth().thinOutline().height(paramsHeight.toDp()),
            readOnly = false,
            singleLine = true,
            label = { Text(stringResource(Res.string.params_text)) },
            colors = TextFieldDefaults.textFieldColors(textColor = MaterialTheme.colors.onSurface),
        )
        Spacer(modifier = Modifier.height(itemSpacing))

        Row(modifier = Modifier.fillMaxWidth()) {
            TextField(
                value = command,
                onValueChange = {},
                modifier = Modifier.height(commandHeight.toDp()).weight(1.0f).thinOutline(),
                readOnly = true,
                label = { Text(stringResource(Res.string.command)) },
                trailingIcon = {
                    IconButton(onClick = { clipboardManager.setText(buildAnnotatedString { append(command) }) }) {
                        Icon(painter = painterResource(Res.drawable.copy_icon), contentDescription = null)
                    }
                },
                colors = TextFieldDefaults.textFieldColors(textColor = MaterialTheme.colors.onSurface)
            )

            if (params.isNotEmpty()) {
                HorizontalSpacer_s()
                ParamsList(params, modifier = Modifier.height(commandHeight.toDp()), {
                    paramsText = it
                    viewModel.paramsTextUpdated(it)
                })
            }
        }

        DraggableVerticalSpacer(size = itemSpacing, defaultOffset = loadedOffset.toFloat(), onDragOffset = { offset = it.toInt() })
        TextField(
            value = output,
            onValueChange = {},
            modifier = Modifier.height(outputHeight.toDp()).fillMaxWidth().thinOutline(),
            readOnly = true,
            label = { Text(stringResource(Res.string.output)) },
            trailingIcon = {
                IconButton(onClick = { clipboardManager.setText(buildAnnotatedString { append(output) }) }) {
                    Icon(painter = painterResource(Res.drawable.copy_icon), contentDescription = null)
                }
            },
            colors = TextFieldDefaults.textFieldColors(textColor = MaterialTheme.colors.onSurface),
        )
    }
}

@Composable
private fun ActionButtons(
    modifier: Modifier,
    viewModel: MainScreenViewModel,
    allowExecution: Boolean,
    hasChanges: Boolean,
    executeState: ExecuteState,
) {
    val exportTitle = stringResource(Res.string.save_configuration_file)
    val fileExistText = stringResource(Res.string.file_already_exist)
    val fileExistTitle = stringResource(Res.string.file_already_exist)
    val openFileTitle = stringResource(Res.string.open_file)
    val fileNotFoundMessage = stringResource(Res.string.file_not_found_message)
    val fileNotFoundTitle = stringResource(Res.string.file_not_found_title)

    BoxWithConstraints {
        val compactMode = maxWidth < COMPACT_MODE_BUTTON_WIDTH

        Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier) {
            CompactableButton(
                text = Res.string.execute,
                icon = Res.drawable.terminal,
                enabled = allowExecution,
                isCompact = compactMode
            ) {
                viewModel.execute()
            }
            HorizontalSpacer_s()
            when (executeState) {
                ExecuteState.NONE -> {}
                ExecuteState.WORKING -> {
                    CircularProgressIndicator(modifier = Modifier.width(32.dp).padding(top = 8.dp), color = MaterialTheme.colors.secondary)
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
            if (hasChanges) {
                Spacer(modifier = Modifier.weight(1.0f))
                CompactableButton(text = Res.string.save, icon = Res.drawable.save, isCompact = compactMode) {
                    viewModel.saveChanges()
                }
                HorizontalSpacer_s()
            } else {
                Spacer(modifier = Modifier.weight(1.0f))
            }

            CompactableButton(text = Res.string.add_app_path, icon = Res.drawable.add, isCompact = compactMode) {
                viewModel.showAddGroupDialogue()
            }
            HorizontalSpacer_s()


            CompactableButton(
                text = Res.string.import,
                icon = Res.drawable.file_import,
                isCompact = compactMode,
            ) {
                val previousWorkingDir = viewModel.settings.getString(IMPORT_PATH) ?: ""
                openFileSwingChooser(
                    title = openFileTitle,
                    currentDir = File(previousWorkingDir),
                    initialFileName = "TerminalExecutorConfiguration.$JSON_EXTENSION",
                    onFileConfirm = { viewModel.import(it) },
                    fileNameExtensionFilter = FileNameExtensionFilter("JSON File (.$JSON_EXTENSION)", JSON_EXTENSION),
                    fileDoesNotExistTitle = fileNotFoundTitle,
                    fileDoesNotExistMessage = fileNotFoundMessage,
                )
            }
            HorizontalSpacer_s()
            CompactableButton(
                text = Res.string.export,
                icon = Res.drawable.save_as,
                isCompact = compactMode
            ) {
                val previousWorkingDir = viewModel.settings.getString(EXPORT_PATH) ?: ""
                saveFileSwingChooser(
                    title = exportTitle,
                    currentDir = File(previousWorkingDir),
                    initialFileName = "TerminalExecutorConfiguration.$JSON_EXTENSION",
                    onFileConfirm = {
                        viewModel.export(it)
                        viewModel.settings.putString(EXPORT_PATH, it.toString())
                    },
                    fileNameExtensionFilter = FileNameExtensionFilter("JSON File (.$JSON_EXTENSION)", JSON_EXTENSION),
                    overwriteMessage = fileExistText,
                    overwriteTitle = fileExistTitle
                )
            }
        }
    }

}