package com.bojan.terminalexecutor.ui.screens

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.outlined.Star
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.bojan.terminalexecutor.ktx.thinOutline
import com.bojan.terminalexecutor.ui.controls.CommandListItem
import com.bojan.terminalexecutor.ui.uistates.ListItemUiState
import com.bojan.terminalexecutor.viewmodel.MainScreenViewModel
import org.jetbrains.compose.resources.painterResource
import terminalexecutor.composeapp.generated.resources.Res
import terminalexecutor.composeapp.generated.resources.compose_multiplatform
import terminalexecutor.composeapp.generated.resources.copy_icon

@Composable
fun MainScreen(viewModel: MainScreenViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colors.surface).padding(8.dp)) {
        ItemList(
            items = uiState.items,
            modifier = Modifier.weight(0.5f),
            onFavorite = { viewModel.itemFavoriteToggle(it) },
            onSelected = { viewModel.itemSelected(it) }
        )
        Spacer(modifier = Modifier.height(8.dp))
        ActionItems(
            modifier = Modifier.weight(0.5f),
            command = uiState.command,
            output = uiState.outputText,
            allowExecution = uiState.allowExecution
        ) { viewModel.execute() }
    }
}

@Composable
fun ItemList(items: List<ListItemUiState>, modifier: Modifier, onFavorite: (String) -> Unit, onSelected: (String) -> Unit) {
    val listState = rememberLazyListState()
    Row(modifier = Modifier.fillMaxWidth().thinOutline().then(modifier)) {
        LazyColumn(state = listState, modifier = Modifier.weight(1.0f)) {
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
        //Box(modifier = Modifier.background(Color.Red).fillMaxHeight().width(10.dp)) {  }
        VerticalScrollbar(adapter = rememberScrollbarAdapter(scrollState = listState), modifier = Modifier.width(14.dp).padding(horizontal = 2.dp, vertical = 1.dp))
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
        TextField(
            value = command,
            onValueChange = {},
            modifier = Modifier.height(100.dp).fillMaxWidth().thinOutline(),
            readOnly = true,
            label = { Text("Command") },
            trailingIcon = {
                IconButton(onClick = {}) {
                    Icon(painter = painterResource(Res.drawable.copy_icon), contentDescription = null)
                }
            }
        )
        Spacer(modifier = Modifier.height(10.dp))
        TextField(
            value = output,
            onValueChange = {},
            modifier = Modifier.weight(1.0f).fillMaxWidth().thinOutline(),
            readOnly = true,
            label = { Text("Output") },
            trailingIcon = {
                IconButton(onClick = {}) {
                    Icon(painter = painterResource(Res.drawable.copy_icon), contentDescription = null)
                }
            }
        )
        Spacer(modifier = Modifier.height(10.dp))
        Button(onClick = { onExecute() }, enabled = allowExecution) {
            Text("Execute")
        }
    }
}