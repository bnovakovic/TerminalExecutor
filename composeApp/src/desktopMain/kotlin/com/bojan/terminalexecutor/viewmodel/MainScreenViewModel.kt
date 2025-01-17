package com.bojan.terminalexecutor.viewmodel

import com.bojan.terminalexecutor.commandexecutor.executeCommand
import com.bojan.terminalexecutor.ui.uistates.ListItemUiState
import com.bojan.terminalexecutor.ui.uistates.MainScreenUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainScreenViewModel {
    private val _uiState = MutableStateFlow(
        MainScreenUiState(
            items = listOf(
                ListItemUiState("Adb devices", listOf("adb", "devices"), false, false),
                ListItemUiState("Git status", listOf("git", "status"), false, false)
            ),
            command = "",
            allowExecution = false,
            outputText = ""
        )
    )
    val uiState = _uiState.asStateFlow()
    private var commandToExecute: Array<String> = emptyArray()

    fun itemFavoriteToggle(name: String) {
        val items = _uiState.value.items
        val newItems = items.map { if (it.name == name) it.copy(isFavorite = !it.isFavorite) else it }
        _uiState.value = _uiState.value.copy(items = newItems)
    }

    fun itemSelected(name: String) {
        val found = _uiState.value.items.find { it.name == name }

        found?.let { foundItem ->
            val separator = " "
            val commandString = foundItem.commands.joinToString(separator)
            val items = _uiState.value.items
            val newItems = items.map {
                val isSelected = it.name == name
                it.copy(isSelected = isSelected)
            }
            _uiState.value = _uiState.value.copy(
                items = newItems,
                command = commandString,
                allowExecution = foundItem.commands.isNotEmpty()
            )
            commandToExecute = foundItem.commands.toTypedArray()
        }
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