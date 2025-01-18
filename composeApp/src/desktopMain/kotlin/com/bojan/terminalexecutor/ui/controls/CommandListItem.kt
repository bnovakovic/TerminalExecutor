package com.bojan.terminalexecutor.ui.controls

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.bojan.terminalexecutor.ui.uistates.ListItemGroupUiState
import com.bojan.terminalexecutor.ui.uistates.ListItemUiState
import org.jetbrains.compose.resources.painterResource
import terminalexecutor.composeapp.generated.resources.Res
import terminalexecutor.composeapp.generated.resources.arrow_down
import terminalexecutor.composeapp.generated.resources.arrow_right

@Composable
fun CommandListItem(
    name: String,
    modifier: Modifier = Modifier,
    onItemSelected: () -> Unit
) {
    Row(modifier = Modifier.clickable { onItemSelected() }.fillMaxWidth().then(modifier), verticalAlignment = Alignment.CenterVertically) {
        Spacer(modifier = Modifier.width(10.dp))
        Text(name)
    }
}

@Composable
fun getSelectableColor(isSelected: Boolean): Color {
    return if (isSelected) {
        MaterialTheme.colors.secondary
    } else {
        Color.Transparent
    }
}

@Composable
fun CommandListGroup(
    text: String,
    items: List<ListItemUiState>,
    children: List<ListItemGroupUiState>,
    modifier: Modifier = Modifier,
    onItemSelected: (List<String>) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Column(modifier = Modifier.fillMaxWidth().then(modifier)) {
        Row(modifier = Modifier.clickable { expanded = !expanded }.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Icon(painterResource(getIcon(expanded)), contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text)
        }
        if (expanded) {
            items.forEach {
                CommandListItem(it.name, Modifier.padding(start = 24.dp)) { onItemSelected(it.commands) }
            }
            children.forEach {
                CommandListGroup(it.text, it.items, it.children, Modifier.padding(start = 24.dp), onItemSelected)
            }
        }
    }
}

fun getIcon(isExpanded: Boolean) = if (isExpanded) Res.drawable.arrow_down else Res.drawable.arrow_right