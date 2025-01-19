package com.bojan.terminalexecutor.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bojan.terminalexecutor.commandexecutor.executeCommand
import com.bojan.terminalexecutor.configmanagers.exportList
import com.bojan.terminalexecutor.configmanagers.importList
import com.bojan.terminalexecutor.enum.ExecuteState
import com.bojan.terminalexecutor.enum.MainScreenDialog
import com.bojan.terminalexecutor.ui.uistates.ItemsUiState
import com.bojan.terminalexecutor.ui.uistates.ListItemGroupUiState
import com.bojan.terminalexecutor.ui.uistates.ListItemUiState
import com.bojan.terminalexecutor.ui.uistates.MainScreenUiState
import com.bojan.terminalexecutor.utils.RandomIdGenerator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File


class MainScreenViewModel(
    val idGenerator: RandomIdGenerator = RandomIdGenerator()
) : ViewModel() {
    private val exampleItems = listOf(
        ListItemGroupUiState(
            id = "0",
            text = "ADB",
            items = listOf(
                ListItemUiState("ADB list devices", listOf("adb", "devices")),
                ListItemUiState("Wrong ADB command", listOf("adb", "programs")),
                ListItemUiState("Wrong adb executable", listOf("adbe", "devices")),
            ),
            children = listOf(
                ListItemGroupUiState(
                    id = "0,0",
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
            id = "1",
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
            items = ItemsUiState(emptyList()),
            command = "",
            allowExecution = false,
            outputText = "",
            executeState = ExecuteState.NONE,
            mainScreenDialog = MainScreenDialog.NONE
        )
    )
    val uiState = _uiState.asStateFlow()
    private var commandToExecute: Array<String> = emptyArray()
    private var storedParentId: String? = null

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
            exportList(uiState.value.items.items, file)
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
            importList(file, idGenerator)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(items = ItemsUiState(it), command = "", outputText = successMessage)
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
}