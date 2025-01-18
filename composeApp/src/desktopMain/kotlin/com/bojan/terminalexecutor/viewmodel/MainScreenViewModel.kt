package com.bojan.terminalexecutor.viewmodel

import com.bojan.terminalexecutor.commandexecutor.executeCommand
import com.bojan.terminalexecutor.ui.uistates.ListItemGroupUiState
import com.bojan.terminalexecutor.ui.uistates.ListItemUiState
import com.bojan.terminalexecutor.ui.uistates.MainScreenUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainScreenViewModel {
    private val _uiState = MutableStateFlow(
        MainScreenUiState(
            items = listOf(
                ListItemGroupUiState(
                    text = "ADB",
                    items = listOf(
                        ListItemUiState("ADB list devices", listOf("adb", "devices"), false),
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
            outputText = ""
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
        executeCommand(commandToExecute)
            .onSuccess {
                _uiState.value = _uiState.value.copy(outputText = "Success: $it")
            }
            .onFailure {
                _uiState.value = _uiState.value.copy(outputText = "Failure: $it")
            }
    }
}