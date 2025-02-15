package com.bojan.terminalexecutor.ui.controls

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import com.bojan.terminalexecutor.HorizontalSpacer_s
import com.bojan.terminalexecutor.ktx.thinOutline

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DropDownMenu(
    selectedValue: Int,
    items: List<String>,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val shouldBeEnabled = items.isNotEmpty() && items.size > 1
    val desiredAlpha = if (shouldBeEnabled) 1.0f else 0.5f
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            expanded = if (shouldBeEnabled) {
                !expanded
            } else {
                false
            }
        },
        modifier = Modifier.alpha(desiredAlpha).thinOutline()
    ) {
        val onlyOneItem = items.size == 1
        Box(
            modifier = modifier
                .fillMaxSize()
                .alpha(desiredAlpha)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                HorizontalSpacer_s()
                Text(
                    text = if (items.size > selectedValue && selectedValue >= 0) items[selectedValue] else "",
                    color = MaterialTheme.colors.onSurface,
                    modifier = Modifier
                        .basicMarquee()
                        .weight(1.0f)
                )
                Image(
                    imageVector = Icons.Filled.ArrowDropDown,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(end = 8.dp)
                        .rotate(
                            if (expanded)
                                180f
                            else
                                360f
                        ),
                    colorFilter = if (!shouldBeEnabled || onlyOneItem) ColorFilter.tint(MaterialTheme.colors.onSurface) else ColorFilter.tint(
                        MaterialTheme.colors.onSurface)
                )
            }
        }

        if (!onlyOneItem) {
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                items.forEachIndexed { index, selected: String ->
                    DropdownMenuItem(
                        content = { Text(text = selected) },
                        onClick = {
                            expanded = false
                            onItemSelected(index)
                        },
                        modifier = Modifier.height(24.dp).background(if (index == selectedValue) Color.LightGray else Color.Unspecified),
                    )
                }
            }
        }
    }
}