package com.bojan.terminalexecutor.ui.uistates

data class ItemsUiState(val items: List<ListItemGroupUiState>) {
    fun addGroup(parentId: String, newGroup: ListItemGroupUiState) {
        println("Adding group. Parent ID: $parentId, new group: $newGroup")
    }

    fun addItem(parentId: String, newItemUiState: ListItemUiState) {
        println("Add item. Parent ID: $parentId, new item: $newItemUiState")
    }
}