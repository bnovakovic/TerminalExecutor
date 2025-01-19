package com.bojan.terminalexecutor.ui.uistates

data class ItemsUiState(val items: List<ListItemGroupUiState>) {
    fun addGroup(parentId: String, newGroup: ListItemGroupUiState): ItemsUiState {
        if (parentId == "") {
            val mutable = items.toMutableList()
            mutable.add(newGroup)
            return ItemsUiState(mutable.toList())
        }
        return ItemsUiState(items.map { addGroupRecursively(it, parentId, newGroup) })
    }

    private fun addGroupRecursively(group: ListItemGroupUiState, parentId: String, newGroup: ListItemGroupUiState): ListItemGroupUiState {
        if (group.id == parentId) {
            return group.copy(children = group.children + newGroup)
        }

        val updatedChildren = group.children.map { addGroupRecursively(it, parentId, newGroup) }
        return group.copy(children = updatedChildren)
    }

    fun addItem(parentId: String, newItemUiState: ListItemUiState): ItemsUiState {
        return ItemsUiState(items.map { addItemRecursively(it, parentId, newItemUiState) })
    }

    private fun addItemRecursively(group: ListItemGroupUiState, parentId: String, newItem: ListItemUiState): ListItemGroupUiState {
        if (group.id == parentId) {
            return group.copy(items = group.items + newItem)
        }

        val updatedChildren = group.children.map { addItemRecursively(it, parentId, newItem) }
        return group.copy(children = updatedChildren)
    }
}