package com.bojan.terminalexecutor

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.bojan.terminalexecutor.ui.screens.MainScreen
import com.bojan.terminalexecutor.viewmodel.MainScreenViewModel
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    MaterialTheme {
        MainScreen(MainScreenViewModel())
    }
}

@Composable
fun View(nodes: List<CommentNode>) {
    val expandedItems = remember { mutableStateListOf<CommentNode>() }
    LazyColumn(modifier = Modifier.fillMaxSize().background(Color.Red)) {
        nodes(
            nodes,
            isExpanded = {
                expandedItems.contains(it)
            },
            toggleExpanded = {
                if (expandedItems.contains(it)) {
                    expandedItems.remove(it)
                } else {
                    expandedItems.add(it)
                }
            },
        )
    }
}

fun LazyListScope.nodes(
    nodes: List<CommentNode>,
    isExpanded: (CommentNode) -> Boolean,
    toggleExpanded: (CommentNode) -> Unit,
) {
    nodes.forEach { node ->
        node(
            node,
            isExpanded = isExpanded,
            toggleExpanded = toggleExpanded,
        )
    }
}

fun LazyListScope.node(
    node: CommentNode,
    isExpanded: (CommentNode) -> Boolean,
    toggleExpanded: (CommentNode) -> Unit,
) {
    item {
        Text(
            node.content,
            Modifier.clickable {
                toggleExpanded(node)
            }
        )
    }
    if (isExpanded(node)) {
        nodes(
            node.children,
            isExpanded = isExpanded,
            toggleExpanded = toggleExpanded,
        )
    }
}

data class CommentNode(val content: String, val children: List<CommentNode>)