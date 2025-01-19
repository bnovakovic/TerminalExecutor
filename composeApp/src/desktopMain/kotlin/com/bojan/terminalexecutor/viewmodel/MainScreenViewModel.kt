package com.bojan.terminalexecutor.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bojan.terminalexecutor.commandexecutor.executeCommand
import com.bojan.terminalexecutor.configmanagers.exportList
import com.bojan.terminalexecutor.configmanagers.importList
import com.bojan.terminalexecutor.enum.ExecuteState
import com.bojan.terminalexecutor.enum.MainScreenDialog
import com.bojan.terminalexecutor.ktx.replaceParams
import com.bojan.terminalexecutor.settings.CONFIGURATION_PATH
import com.bojan.terminalexecutor.settings.EXPORT_PATH
import com.bojan.terminalexecutor.settings.IMPORT_PATH
import com.bojan.terminalexecutor.settings.IS_IN_DARK_MODE
import com.bojan.terminalexecutor.settings.TerminalExecutorSettings
import com.bojan.terminalexecutor.settings.WORKING_DIR
import com.bojan.terminalexecutor.ui.uistates.ItemsUiState
import com.bojan.terminalexecutor.ui.uistates.ListItemGroupUiState
import com.bojan.terminalexecutor.ui.uistates.ListItemUiState
import com.bojan.terminalexecutor.ui.uistates.MainScreenUiState
import com.bojan.terminalexecutor.utils.RandomIdGenerator
import com.bojan.terminalexecutor.utils.getCurrentDir
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File


class MainScreenViewModel(
    val idGenerator: RandomIdGenerator = RandomIdGenerator(),
    val settings: TerminalExecutorSettings = TerminalExecutorSettings(),
    private val onThemeChanged: (Boolean) -> Unit
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        MainScreenUiState(
            items = ItemsUiState(emptyList()),
            command = "",
            allowExecution = false,
            outputText = "",
            executeState = ExecuteState.NONE,
            mainScreenDialog = MainScreenDialog.NONE,
            workingDirectory = getCurrentDir()
        )
    )
    val uiState = _uiState.asStateFlow()
    private var commandToExecute: Array<String> = emptyArray()
    private var storedParentId: String? = null
    private var currentParams: String = ""

    private var doubleClickActive: Boolean = false
    private var clickTimerJob: Job? = null

    init {
        settings.loadSettings()
        onThemeChanged(settings.getBoolean(IS_IN_DARK_MODE) ?: false)
        settings.getString(CONFIGURATION_PATH)?.let {
            import(File(it))
        }
        settings.getString(WORKING_DIR)?.let {
            _uiState.value = _uiState.value.copy(workingDirectory = File(it))
        }
    }

    fun itemSelected(commands: List<String>) {
        val commandsToArray = commands.toTypedArray()

        // Unfortunately double click either causes lag or issues in compose, so I had to use this non standard way of detecting it.
        if (doubleClickActive && commandsToArray.contentEquals(commandToExecute) && _uiState.value.allowExecution) {
            execute()
        } else {
            commandToExecute = commandsToArray
            _uiState.value = _uiState.value.copy(
                command = generateCommandText(),
                allowExecution = commands.isNotEmpty()
            )
            doubleClickActive = true
            startDoubleClickTimerReset()
        }
    }

    fun execute() {
        _uiState.value = _uiState.value.copy(executeState = ExecuteState.WORKING, allowExecution = false, outputText = "")
        val addedParams = commandToExecute.replaceParams(currentParams)
        viewModelScope.launch {
            executeCommand(addedParams, _uiState.value.workingDirectory)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(outputText = it, executeState = ExecuteState.OK, allowExecution = true)
                }
                .onFailure {
                    _uiState.value = _uiState.value.copy(outputText = it, executeState = ExecuteState.ERROR, allowExecution = true)
                }
        }
    }

    fun export(file: File) {
        _uiState.value = _uiState.value.copy(allowExecution = false)
        viewModelScope.launch {
            settings.putString(EXPORT_PATH, file.toString())
            exportList(uiState.value.items.items, file)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(outputText = "")
                }
                .onFailure {
                    _uiState.value = _uiState.value.copy(outputText = it.message?: "")
                }
            _uiState.value = _uiState.value.copy(allowExecution = true)
        }
    }

    fun import(file: File) {
        viewModelScope.launch {
            settings.putString(IMPORT_PATH, file.toString())
            importList(file, idGenerator)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(items = ItemsUiState(it), command = "", outputText = "")
                    settings.putString(CONFIGURATION_PATH, file.toString())
                }
                .onFailure {
                    _uiState.value = _uiState.value.copy(outputText = it.message?: "", command = "")
                }
        }
    }

    fun showAddItemDialogue(parentId: String) {
        if (parentId != "") {
            _uiState.value = _uiState.value.copy(mainScreenDialog = MainScreenDialog.ADD_ANY_ITEM)
        } else {
            _uiState.value = _uiState.value.copy(mainScreenDialog = MainScreenDialog.ADD_GROUP)
        }
        storedParentId = parentId
    }

    fun hideDialogue() {
        _uiState.value = _uiState.value.copy(mainScreenDialog = MainScreenDialog.NONE)
        storedParentId = null
    }

    fun addGroup(listItemGroupUiState: ListItemGroupUiState) {
        storedParentId?.let {
            val newItems = _uiState.value.items.addGroup(it, listItemGroupUiState)
            _uiState.value = _uiState.value.copy(items = newItems)
        }
        hideDialogue()
    }

    fun addItem(listItemUiState: ListItemUiState) {
        storedParentId?.let {
            val newItems = _uiState.value.items.addItem(it, listItemUiState)
            _uiState.value = _uiState.value.copy(items = newItems)
        }
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

    private fun startDoubleClickTimerReset() {
        clickTimerJob = viewModelScope.launch {
            delay(DOUBLE_CLICK_DELAY)
            doubleClickActive = false
        }
    }

    companion object {
        const val DOUBLE_CLICK_DELAY = 300L
    }
}