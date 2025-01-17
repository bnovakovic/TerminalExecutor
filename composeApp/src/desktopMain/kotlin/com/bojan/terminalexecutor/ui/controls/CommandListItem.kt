package com.bojan.terminalexecutor.ui.controls

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun CommandListItem(
    name: String,
    isFavorite: Boolean,
    isSelected: Boolean,
    onFavoriteClick: () -> Unit,
    onItemSelected: () -> Unit
) {
    Row(modifier = Modifier.clickable { onItemSelected() }.fillMaxWidth().background(getSelectableColor(isSelected)), verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = { onFavoriteClick() }) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = if (isFavorite) MaterialTheme.colors.primary else Color.Gray
            )
        }
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