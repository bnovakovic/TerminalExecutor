package com.bojan.terminalexecutor.seriazible

import kotlinx.serialization.Serializable

@Serializable
data class ItemList(val items: List<ListItemGroupData>)