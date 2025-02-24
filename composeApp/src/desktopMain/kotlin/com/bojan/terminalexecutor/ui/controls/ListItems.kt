package com.bojan.terminalexecutor.ui.controls

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import com.bojan.terminalexecutor.HorizontalSpacer_s
import com.bojan.terminalexecutor.HorizontalSpacer_xs
import com.bojan.terminalexecutor.ktx.doubleClickable
import com.bojan.terminalexecutor.ui.uistates.ListItemGroupUiState
import com.bojan.terminalexecutor.ui.uistates.ParamInfoUiState
import org.jetbrains.compose.resources.painterResource
import terminalexecutor.composeapp.generated.resources.Res
import terminalexecutor.composeapp.generated.resources.add
import terminalexecutor.composeapp.generated.resources.arrow_down
import terminalexecutor.composeapp.generated.resources.arrow_right

@Composable
fun CommandListItem(
    name: String,
    modifier: Modifier = Modifier,
    onDelete: () -> Unit,
    onItemSelected: () -> Unit,
    onDoubleClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .doubleClickable(onClick = onItemSelected, onDoubleClick = onDoubleClick)
            .fillMaxWidth().then(modifier),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HorizontalSpacer_s()
        Text(name, color = MaterialTheme.colors.onSurface)
        Spacer(modifier = Modifier.weight(1.0f))
        Image(
            Icons.Default.Delete,
            contentDescription = null,
            Modifier.size(16.dp).clickable { onDelete() },
            colorFilter = ColorFilter.tint(MaterialTheme.colors.onSurface)
        )
        HorizontalSpacer_xs()
    }
}

@Composable
fun AddRootItem(
    text: String,
    onItemSelected: () -> Unit
) {
    Row(modifier = Modifier.clickable { onItemSelected() }.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        HorizontalSpacer_s()
        Image(
            painter = painterResource(Res.drawable.add),
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            colorFilter = ColorFilter.tint(MaterialTheme.colors.onSurface)
        )
        HorizontalSpacer_s()
        Text(text, color = MaterialTheme.colors.onSurface)
    }
}

@Composable
fun CommandListGroup(
    groupUiState: ListItemGroupUiState,
    modifier: Modifier = Modifier,
    expanded: Boolean,
    expandedMap: Map<String, Boolean>,
    onExpand: (String, Boolean) -> Unit,
    onDeleteItem: (ListItemGroupUiState, Int) -> Unit,
    onDeleteGroup: (ListItemGroupUiState) -> Unit,
    onAddItem: (String) -> Unit,
    onItemSelected: (commands: List<String>, params: List<ParamInfoUiState>) -> Unit,
    onDoubleClick: () -> Unit
) {

    Column(modifier = Modifier.fillMaxWidth().then(modifier)) {
        Row(
            modifier = Modifier.clickable { onExpand(groupUiState.id, !expanded) }.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painterResource(getIcon(expanded)),
                contentDescription = null,
                tint = MaterialTheme.colors.onSurface
            )
            HorizontalSpacer_s()
            Text(groupUiState.text, color = MaterialTheme.colors.onSurface)
            HorizontalSpacer_xs()
            Image(
                painter = painterResource(Res.drawable.add),
                contentDescription = null,
                modifier = Modifier.size(16.dp).clickable { onAddItem(groupUiState.id) },
                colorFilter = ColorFilter.tint(MaterialTheme.colors.onSurface)
            )
            Spacer(modifier = Modifier.weight(1.0f))
            Image(
                Icons.Default.Delete,
                contentDescription = null,
                Modifier.size(16.dp).clickable { onDeleteGroup(groupUiState) },
                colorFilter = ColorFilter.tint(MaterialTheme.colors.onSurface)
            )
            HorizontalSpacer_xs()
        }
        if (expanded) {
            groupUiState.items.forEachIndexed { index, item ->
                CommandListItem(
                    name = item.name,
                    modifier = Modifier.padding(start = 24.dp),
                    onDelete = { onDeleteItem(groupUiState, index) },
                    onItemSelected = { onItemSelected(item.commands, item.params) },
                    onDoubleClick = onDoubleClick
                )
            }
            groupUiState.children.forEach { group ->
                val isChildExpanded = expandedMap[group.id] ?: false
                CommandListGroup(
                    groupUiState = group,
                    modifier = Modifier.padding(start = 24.dp),
                    onAddItem = { thisId ->
                        onAddItem(thisId)
                    },
                    expanded = isChildExpanded,
                    onExpand = { id, groupExpanded -> onExpand(id, groupExpanded) },
                    onItemSelected = onItemSelected,
                    onDeleteItem = onDeleteItem,
                    onDeleteGroup = { onDeleteGroup(it) },
                    expandedMap = expandedMap,
                    onDoubleClick = onDoubleClick
                )
            }
        }
    }
}

fun getIcon(isExpanded: Boolean) = if (isExpanded) Res.drawable.arrow_down else Res.drawable.arrow_right