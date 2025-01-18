package com.bojan.terminalexecutor.configmanagers

import com.bojan.terminalexecutor.ktx.toListItemGroupUiState
import com.bojan.terminalexecutor.seriazible.ItemList
import com.bojan.terminalexecutor.ui.uistates.ListItemGroupUiState
import com.bojan.terminalexecutor.utils.RandomIdGenerator
import kotlinx.serialization.json.Json
import java.io.File

suspend fun importList(file: File, idGenerator: RandomIdGenerator): Result<List<ListItemGroupUiState>> {
    return try {
        val imported = Json.decodeFromString<ItemList>(file.readText())
        val uiState = imported.items.mapIndexed { index, listItemGroupData ->  listItemGroupData.toListItemGroupUiState(idGenerator) }
        Result.success(uiState)
    } catch (e: Exception) {
        e.printStackTrace()
        Result.failure(e)
    }

}