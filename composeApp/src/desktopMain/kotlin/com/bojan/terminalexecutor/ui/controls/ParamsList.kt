package com.bojan.terminalexecutor.ui.controls

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bojan.terminalexecutor.HorizontalSpacer_s
import com.bojan.terminalexecutor.HorizontalSpacer_xs
import com.bojan.terminalexecutor.ktx.thinOutline
import com.bojan.terminalexecutor.ui.uistates.ParamInfoUiState

@Composable
fun ParamsList(items: List<ParamInfoUiState>, modifier: Modifier, onItemSelected: (String) -> Unit) {
    val scrollState = rememberLazyListState()
    Row(modifier = Modifier.thinOutline().width(200.dp).then(modifier)) {
        LazyColumn(state = scrollState, modifier = Modifier.weight(1.0f)) {
            items(items) { item ->
                Row(modifier = Modifier.clickable { onItemSelected(item.value) }.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    HorizontalSpacer_s()
                    Text(item.name, color = MaterialTheme.colors.onSurface)
                    HorizontalSpacer_xs()
                }
            }
        }

        VerticalScrollbar(
            adapter = rememberScrollbarAdapter(scrollState = scrollState),
            modifier = Modifier.width(14.dp).padding(horizontal = 2.dp, vertical = 1.dp)
        )
    }

}