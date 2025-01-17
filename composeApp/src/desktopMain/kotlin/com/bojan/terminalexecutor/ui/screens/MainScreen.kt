package com.bojan.terminalexecutor.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bojan.terminalexecutor.ui.controls.CommandListItem
import com.bojan.terminalexecutor.ui.uistates.ListItemUiState
import com.bojan.terminalexecutor.viewmodel.MainScreenViewModel

@Composable
fun MainScreen(viewModel: MainScreenViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colors.surface)) {
        ItemList(
            items = uiState.items,
            modifier = Modifier.weight(0.7f),
            onFavorite = { viewModel.itemFavoriteToggle(it) },
            onSelected = { viewModel.itemSelected(it) }
        )
        ActionItems(
            modifier = Modifier.weight(0.3f),
            command = uiState.command,
            output = uiState.outputText,
            allowExecution = uiState.allowExecution
        ) { viewModel.execute() }
    }
}

@Composable
fun ItemList(items: List<ListItemUiState>, modifier: Modifier, onFavorite: (String) -> Unit, onSelected: (String) -> Unit) {
    LazyColumn(modifier = Modifier.fillMaxWidth().then(modifier)) {
        items(items) { item ->
            CommandListItem(
                name = item.name,
                isFavorite = item.isFavorite,
                isSelected = item.isSelected,
                onFavoriteClick = { onFavorite(item.name) },
                onItemSelected = { onSelected(item.name) }
            )
        }
    }
}

@Composable
fun ActionItems(
    modifier: Modifier,
    command: String,
    output: String,
    allowExecution: Boolean,
    onExecute: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().then(modifier)) {
        Text("Command: $command")
        Spacer(modifier = Modifier.height(10.dp))
        TextField(value = "Output: $output", onValueChange = {}, modifier = Modifier.weight(1.0f).fillMaxWidth(), readOnly = true)
        Spacer(modifier = Modifier.height(10.dp))
        Button(onClick = { onExecute() }, enabled = allowExecution) {
            Text("Execute")
        }
    }
}