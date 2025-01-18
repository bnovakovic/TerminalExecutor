package com.bojan.terminalexecutor.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bojan.terminalexecutor.commandexecutor.executeCommand
import com.bojan.terminalexecutor.configmanagers.exportList
import com.bojan.terminalexecutor.configmanagers.importList
import com.bojan.terminalexecutor.enum.ExecuteState
import com.bojan.terminalexecutor.enum.MainScreenDialog
import com.bojan.terminalexecutor.ui.uistates.ListItemGroupUiState
import com.bojan.terminalexecutor.ui.uistates.ListItemUiState
import com.bojan.terminalexecutor.ui.uistates.MainScreenUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File


class MainScreenViewModel : ViewModel() {
    val exampleItems = listOf(
        ListItemGroupUiState(
            text = "ADB",
            items = listOf(
                ListItemUiState("ADB list devices", listOf("adb", "devices")),
                ListItemUiState("Wrong ADB command", listOf("adb", "programs")),
                ListItemUiState("Wrong adb executable", listOf("adbe", "devices")),
            ),
            children = listOf(
                ListItemGroupUiState(
                    text = "Server",
                    items = listOf(
                        ListItemUiState("ADB Kill Server", listOf("adb", "kill-server")),
                        ListItemUiState("ADB Start Server", listOf("adb", "start-server")),
                    ),
                    children = emptyList()
                )
            )
        ),
        ListItemGroupUiState(
            text = "GIT",
            items = listOf(
                ListItemUiState("Git status", listOf("git", "status")),
                ListItemUiState("Git read remote config", listOf("git", "config", "--get", "remote.origin.url")),
                ListItemUiState("Git show remote", listOf("git", "remote", "show", "origin")),
            ),
            children = emptyList()
        )

    )
    private val _uiState = MutableStateFlow(
        MainScreenUiState(
            items = exampleItems,
            command = "",
            allowExecution = false,
            outputText = "",
            executeState = ExecuteState.NONE,
            mainScreenDialog = MainScreenDialog.NONE
        )
    )
    val uiState = _uiState.asStateFlow()
    private var commandToExecute: Array<String> = emptyArray()

    fun itemSelected(commands: List<String>) {
        val separator = " "
        val commandString = commands.joinToString(separator)
        _uiState.value = _uiState.value.copy(
            command = commandString,
            allowExecution = commands.isNotEmpty()
        )
        commandToExecute = commands.toTypedArray()
    }

    fun execute(commandFiledPrefix: String, commandErrorPrefix: String) {
        _uiState.value = _uiState.value.copy(executeState = ExecuteState.WORKING, allowExecution = false, outputText = "")
        viewModelScope.launch {
            executeCommand(commandToExecute, commandFiledPrefix, commandErrorPrefix)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(outputText = it, executeState = ExecuteState.OK, allowExecution = true)
                }
                .onFailure {
                    _uiState.value = _uiState.value.copy(outputText = it, executeState = ExecuteState.ERROR, allowExecution = true)
                }
        }
    }

    fun export(file: File, successMessage: String) {
        _uiState.value = _uiState.value.copy(allowExecution = false)
        viewModelScope.launch {
            exportList(uiState.value.items, file)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(outputText = successMessage)
                }
                .onFailure {
                    _uiState.value = _uiState.value.copy(outputText = it.message?: "")
                }
            _uiState.value = _uiState.value.copy(allowExecution = true)
        }
    }

    fun import(file: File, successMessage: String) {
        viewModelScope.launch {
            importList(file)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(items = it, command = "", outputText = successMessage)
                }
                .onFailure {
                    _uiState.value = _uiState.value.copy(outputText = it.message?: "", command = "")
                }
        }
    }

    fun showAddItemDialogue() {
        _uiState.value = _uiState.value.copy(mainScreenDialog = MainScreenDialog.ADD_ITEM)
    }

    fun hideDialogue() {
        _uiState.value = _uiState.value.copy(mainScreenDialog = MainScreenDialog.NONE)
    }
}