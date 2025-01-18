package com.bojan.terminalexecutor.seriazible

import kotlinx.serialization.Serializable

@Serializable
data class ListItemData(
    val name: String,
    val commands: List<String>,
)