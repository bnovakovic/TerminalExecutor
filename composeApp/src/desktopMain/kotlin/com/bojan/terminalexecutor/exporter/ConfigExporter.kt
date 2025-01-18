package com.bojan.terminalexecutor.exporter

import com.bojan.terminalexecutor.constants.JSON_EXTENSION
import com.bojan.terminalexecutor.ktx.toListItemGroupData
import com.bojan.terminalexecutor.seriazible.ListItemGroupData
import com.bojan.terminalexecutor.ui.uistates.ListItemGroupUiState
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

suspend fun exportList(itemData: List<ListItemGroupUiState>, file: File) {
    val finalFile = if (file.extension != JSON_EXTENSION) File("$file.$JSON_EXTENSION") else file
    val converted = ItemList(itemData.map { it.toListItemGroupData() })
    val json = Json { prettyPrint = true }
    val jsonString = json.encodeToString(converted)
    finalFile.writeText(jsonString)
}

@Serializable
data class ItemList(val items: List<ListItemGroupData>)