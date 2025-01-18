package com.bojan.terminalexecutor.seriazible

import kotlinx.serialization.Serializable

@Serializable
data class ListItemGroupData (
    val text: String,
    val items: List<ListItemData>,
    val children: List<ListItemGroupData>
)