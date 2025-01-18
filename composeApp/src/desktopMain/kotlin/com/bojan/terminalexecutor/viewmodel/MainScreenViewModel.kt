package com.bojan.terminalexecutor.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bojan.terminalexecutor.commandexecutor.executeCommand
import com.bojan.terminalexecutor.ui.uistates.ExecuteState
import com.bojan.terminalexecutor.ui.uistates.ListItemGroupUiState
import com.bojan.terminalexecutor.ui.uistates.ListItemUiState
import com.bojan.terminalexecutor.ui.uistates.MainScreenUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainScreenViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(
        MainScreenUiState(
            items = listOf(
                ListItemGroupUiState(
                    text = "ADB",
                    items = listOf(
                        ListItemUiState("ADB list devices", listOf("adb", "devices"), false),
                        ListItemUiState("Wrong ADB command", listOf("adb", "programs"), false),
                        ListItemUiState("Wrong adb executable", listOf("adbe", "devices"), false),
                    ),
                    children = listOf(
                        ListItemGroupUiState(
                            text = "Server",
                            items = listOf(
                                ListItemUiState("ADB Kill Server", listOf("adb", "kill-server"), false),
                                ListItemUiState("ADB Start Server", listOf("adb", "start-server"), false),
                            ),
                            children = emptyList()
                        )
                    )
                ),
                ListItemGroupUiState(
                    text = "GIT",
                    items = listOf(
                        ListItemUiState("Git status", listOf("git", "status"), false),
                        ListItemUiState("Git read remote config", listOf("git", "config", "--get", "remote.origin.url"), false),
                        ListItemUiState("Git show remote", listOf("git", "remote", "show", "origin"), false),
                    ),
                    children = emptyList()
                )

            ),
            command = "",
            allowExecution = false,
            outputText = "",
            executeState = ExecuteState.NONE
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

    fun execute() {
        _uiState.value = _uiState.value.copy(executeState = ExecuteState.WORKING, allowExecution = false, outputText = "")
        viewModelScope.launch {
            executeCommand(commandToExecute)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(outputText = it, executeState = ExecuteState.OK, allowExecution = true)
                }
                .onFailure {
                    _uiState.value = _uiState.value.copy(outputText = it, executeState = ExecuteState.ERROR, allowExecution = true)
                }
        }
    }
}