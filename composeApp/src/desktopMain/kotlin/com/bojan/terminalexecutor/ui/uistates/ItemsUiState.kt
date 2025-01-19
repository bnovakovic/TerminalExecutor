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


    fun removeGroup(group: ListItemGroupUiState): ItemsUiState {
        val updatedItems = items.map { itemGroup ->
            itemGroup.removeChildGroup(group)
        }.filter { it != group }

        return ItemsUiState(updatedItems)
    }

    private fun ListItemGroupUiState.removeChildGroup(group: ListItemGroupUiState): ListItemGroupUiState {
        val updatedChildren = children.map { childGroup ->
            childGroup.removeChildGroup(group)
        }.filter { it != group }
        return this.copy(children = updatedChildren)
    }


    fun removeItem(parent: ListItemGroupUiState, itemIndex: Int): ItemsUiState {
        val updatedGroups = items.map { it.removeItemRecursively(parent, itemIndex) }
        return copy(items = updatedGroups)
    }

    private fun ListItemGroupUiState.removeItemRecursively(parent: ListItemGroupUiState, itemIndex: Int): ListItemGroupUiState {
        val updatedItems = if (this.id == parent.id) {
            if (itemIndex in items.indices) {
                items.filterIndexed { index, _ -> index != itemIndex }
            } else {
                items
            }
        } else {
            items
        }

        val updatedChildren = children.map { it.removeItemRecursively(parent, itemIndex) }
        return this.copy(items = updatedItems, children = updatedChildren)
    }
}