package com.bojan.terminalexecutor.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bojan.terminalexecutor.AppStateInfo
import com.bojan.terminalexecutor.commandexecutor.executeCommand
import com.bojan.terminalexecutor.configmanagers.exportList
import com.bojan.terminalexecutor.configmanagers.importList
import com.bojan.terminalexecutor.enum.ExecuteState
import com.bojan.terminalexecutor.enum.MainScreenDialog
import com.bojan.terminalexecutor.ktx.replaceParams
import com.bojan.terminalexecutor.settings.APP_PATHS
import com.bojan.terminalexecutor.settings.IMPORT_PATH
import com.bojan.terminalexecutor.settings.IS_IN_DARK_MODE
import com.bojan.terminalexecutor.settings.TerminalExecutorSettings
import com.bojan.terminalexecutor.settings.WORKING_DIR
import com.bojan.terminalexecutor.ui.uistates.ItemsUiState
import com.bojan.terminalexecutor.ui.uistates.ListItemGroupUiState
import com.bojan.terminalexecutor.ui.uistates.ListItemUiState
import com.bojan.terminalexecutor.ui.uistates.MainScreenUiState
import com.bojan.terminalexecutor.ui.uistates.ParamInfoUiState
import com.bojan.terminalexecutor.utils.RandomIdGenerator
import com.bojan.terminalexecutor.utils.getConfigFile
import com.bojan.terminalexecutor.utils.getCurrentDir
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.util.Timer
import java.util.TimerTask


class MainScreenViewModel(
    val idGenerator: RandomIdGenerator = RandomIdGenerator(),
    val settings: TerminalExecutorSettings,
    val appStateInfo: AppStateInfo,
    private val onThemeChanged: (Boolean) -> Unit
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        MainScreenUiState(
            items = ItemsUiState(emptyList()),
            command = "",
            allowExecution = false,
            outputText = "",
            executeState = ExecuteState.NONE,
            mainScreenDialog = MainScreenDialog.None,
            workingDirectory = getCurrentDir(),
            changesMade = false,
            groupExpanded = emptyMap(),
            adbDevices = emptyList(),
            selectedDevice = 0,
            isAdbCommand = false,
            paramsList = emptyList()
        )
    )
    val uiState = _uiState.asStateFlow()
    private var commandToExecute: List<String> = emptyList()
    private var storedParentId: String? = null
    private var currentParams: String = ""

    private val deviceCheckTimer: Timer = Timer()
    private var deviceReadDone = true

    init {
        settings.loadSettings()
        onThemeChanged(settings.getBoolean(IS_IN_DARK_MODE) ?: false)
        if (getConfigFile().exists()) {
            viewModelScope.launch {
                loadConfig()
            }
        }
        settings.getString(WORKING_DIR)?.let {
            _uiState.value = _uiState.value.copy(workingDirectory = File(it))
        }

        deviceCheckTimer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                // just safety measure in case task is  stuck, so that we do not start stacking coroutines
                if (deviceReadDone) {
                    deviceReadDone = false
                    viewModelScope.launch {
                        devicesRefreshed(getDeviceList())
                    }
                }
            }
        }, 0, ADB_DEVICE_REFRESH_DELAY)

    }

    fun onAdbDeviceSelected(deviceIndex: Int) {
        _uiState.value = _uiState.value.copy(selectedDevice = deviceIndex)
    }

    fun changeCommand(commands: List<String>, params: List<ParamInfoUiState>) {
        val isAdbCommand = commands.isNotEmpty() && commands[0].lowercase() == ADB_COMMAND
        commandToExecute = commands
        _uiState.value = _uiState.value.copy(
            command = generateCommandText(),
            allowExecution = commands.isNotEmpty() && _uiState.value.executeState != ExecuteState.WORKING,
            isAdbCommand = isAdbCommand,
            paramsList = params
        )
    }

    fun execute() {
        if (commandToExecute.isNotEmpty() && _uiState.value.executeState != ExecuteState.WORKING) {
            _uiState.value = _uiState.value.copy(executeState = ExecuteState.WORKING, allowExecution = false, outputText = "")
            val mutated = mutateAdbCommandForDeviceSelection(oldCommand = commandToExecute)
            val addedParams = mutated.replaceParams(currentParams)
            viewModelScope.launch {
                println("Executing: ${addedParams.joinToString(" ")}")
                executeCommand(addedParams, _uiState.value.workingDirectory, settings.getMap(APP_PATHS))
                    .onSuccess {
                        _uiState.value = _uiState.value.copy(outputText = it, executeState = ExecuteState.OK, allowExecution = true)
                    }
                    .onFailure {
                        _uiState.value = _uiState.value.copy(outputText = it, executeState = ExecuteState.ERROR, allowExecution = true)
                    }
            }
        }
    }

    fun export(file: File) {
        _uiState.value = _uiState.value.copy(allowExecution = false)
        viewModelScope.launch {
            exportList(uiState.value.items.items, file)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(outputText = "")
                }
                .onFailure {
                    _uiState.value = _uiState.value.copy(outputText = it.message ?: "")
                }
            _uiState.value = _uiState.value.copy(allowExecution = commandToExecute.isNotEmpty())
        }
    }

    fun import(file: File) {
        viewModelScope.launch {
            if (file.exists()) {
                val configFile = getConfigFile()
                file.copyTo(configFile, overwrite = true)
                settings.putString(IMPORT_PATH, file.toString())
                loadConfig()
            }
        }
    }

    private suspend fun loadConfig() {
        importList(getConfigFile(), idGenerator)
            .onSuccess {
                _uiState.value = _uiState.value.copy(items = ItemsUiState(it), command = "", outputText = "", changesMade = false)
            }
            .onFailure {
                _uiState.value = _uiState.value.copy(outputText = it.message ?: "", command = "")
            }
        idGenerator.printStoredIds()
    }

    fun showAddItemDialogue(parentId: String) {
        if (parentId != "") {
            _uiState.value = _uiState.value.copy(mainScreenDialog = MainScreenDialog.AddAnyItem)
        } else {
            _uiState.value = _uiState.value.copy(mainScreenDialog = MainScreenDialog.AddGroup)
        }
        storedParentId = parentId
    }

    fun hideDialogue() {
        _uiState.value = _uiState.value.copy(mainScreenDialog = MainScreenDialog.None)
        storedParentId = null
    }

    fun addGroup(listItemGroupUiState: ListItemGroupUiState) {
        storedParentId?.let {
            val newItems = _uiState.value.items.addGroup(it, listItemGroupUiState)
            _uiState.value = _uiState.value.copy(items = newItems)
        }
        changesMade()
        hideDialogue()
    }

    fun showAddGroupDialogue() {
        _uiState.value = _uiState.value.copy(mainScreenDialog = MainScreenDialog.AddAppPath)
    }

    fun addAppPath(app: String, path: String) {
        settings.putMapItem(APP_PATHS, app to path)
        hideDialogue()
    }

    fun addItem(listItemUiState: ListItemUiState) {
        storedParentId?.let {
            val newItems = _uiState.value.items.addItem(it, listItemUiState)
            _uiState.value = _uiState.value.copy(items = newItems)
        }
        changesMade()
        hideDialogue()
    }

    fun workingDirChange(newDir: File) {
        _uiState.value = _uiState.value.copy(workingDirectory = newDir)
        settings.putString(WORKING_DIR, newDir.toString())
    }

    fun paramsTextUpdated(newParams: String) {
        currentParams = newParams
        _uiState.value = _uiState.value.copy(command = generateCommandText())
    }

    fun changeTheme(isDark: Boolean) {
        onThemeChanged(isDark)
        settings.putBoolean(IS_IN_DARK_MODE, isDark)
    }

    fun askDeleteGroup(groupUiState: ListItemGroupUiState) {
        _uiState.value = _uiState.value.copy(mainScreenDialog = MainScreenDialog.DeleteGroup(groupUiState))
    }

    fun confirmDeleteGroup(groupUiState: ListItemGroupUiState) {
        val updatedItems = _uiState.value.items.removeGroup(groupUiState)
        _uiState.value = _uiState.value.copy(
            items = updatedItems,
            mainScreenDialog = MainScreenDialog.None,
            outputText = "",
            command = "",
            allowExecution = false
        )
        commandToExecute = emptyList()
        changesMade()
    }

    fun askDeleteItem(parent: ListItemGroupUiState, itemIndex: Int) {
        _uiState.value = _uiState.value.copy(mainScreenDialog = MainScreenDialog.DeleteItem(parent, itemIndex))
    }

    fun confirmDeleteItem(parent: ListItemGroupUiState, itemIndex: Int) {
        val updatedItems = _uiState.value.items.removeItem(parent, itemIndex)
        _uiState.value = _uiState.value.copy(
            items = updatedItems,
            mainScreenDialog = MainScreenDialog.None,
            outputText = "",
            command = "",
            allowExecution = false
        )
        commandToExecute = emptyList()
        changesMade()
    }

    fun saveChanges() {
        export(getConfigFile())
        changesSaved()
    }

    fun expandCollapseGroup(id: String, expanded: Boolean) {
        val oldUiState = _uiState.value.copy()
        val oldMapAsMutable = oldUiState.groupExpanded.toMutableMap()
        oldMapAsMutable[id] = expanded
        _uiState.value = _uiState.value.copy(groupExpanded = oldMapAsMutable.toMap())
    }

    private fun generateCommandText(): String {
        return if (commandToExecute.isNotEmpty()) {
            val separator = " "
            val commandString = commandToExecute.joinToString(separator)
            val withParams = commandToExecute.replaceParams(currentParams).joinToString(separator = separator)
            if (currentParams.trim().isNotEmpty()) {
                "$commandString\n($withParams)"
            } else {
                commandString
            }
        } else {
            ""
        }
    }

    private fun changesMade() {
        appStateInfo.changesMade = true
        _uiState.value = _uiState.value.copy(changesMade = true)
    }

    private fun changesSaved() {
        appStateInfo.changesMade = false
        _uiState.value = _uiState.value.copy(changesMade = false)
    }

    private suspend fun getDeviceList(): List<String> {
        var deviceList = listOf<String>()
        executeCommand(GET_DEVICES_COMMAND, _uiState.value.workingDirectory, settings.getMap(APP_PATHS))
            .onSuccess { output ->
                val stringToUse = output.replace("\t", " ")
                val outputAsList = stringToUse
                    .split("\n")
                    .map { it.split(" ").first() }
                    .filter { it.trim().isNotBlank() }
                    .toMutableList()
                outputAsList.removeAt(0)
                deviceList = outputAsList
            }
        deviceReadDone = true
        return deviceList
    }

    private fun devicesRefreshed(newDeviceList: List<String>) {
        val oldDeviceList = _uiState.value.adbDevices
        if (newDeviceList.size < oldDeviceList.size) {
            // newDeviceList.size is OK because we always have "none" as an option
            _uiState.value = _uiState.value.copy(adbDevices = newDeviceList, selectedDevice = newDeviceList.size)
        } else {
            _uiState.value = _uiState.value.copy(adbDevices = newDeviceList)
        }
    }

    private fun mutateAdbCommandForDeviceSelection(oldCommand: List<String>): List<String> {
        val selectedDevice = _uiState.value.selectedDevice
        val devices = _uiState.value.adbDevices
        return if (selectedDevice != 0 && devices.size >= selectedDevice) {
            listOf(ADB_COMMAND, ADB_DEVICE_PREFIX, devices[selectedDevice - 1]) + oldCommand.drop(1)
        } else {
            oldCommand
        }

    }

    companion object {
        const val ADB_DEVICE_REFRESH_DELAY = 1000L
        const val ADB_COMMAND = "adb"
        const val ADB_DEVICE_PREFIX = "-s"
        val GET_DEVICES_COMMAND = listOf(ADB_COMMAND, "devices")
    }
}