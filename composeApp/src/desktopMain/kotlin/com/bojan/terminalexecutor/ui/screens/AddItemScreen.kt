package com.bojan.terminalexecutor.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.bojan.terminalexecutor.ktx.thinOutline
import com.bojan.terminalexecutor.ui.uistates.ListItemGroupUiState
import com.bojan.terminalexecutor.ui.uistates.ListItemUiState
import com.bojan.terminalexecutor.utils.RandomIdGenerator
import org.jetbrains.compose.resources.stringResource
import terminalexecutor.composeapp.generated.resources.Res
import terminalexecutor.composeapp.generated.resources.cancel
import terminalexecutor.composeapp.generated.resources.command
import terminalexecutor.composeapp.generated.resources.enter_group_name
import terminalexecutor.composeapp.generated.resources.group
import terminalexecutor.composeapp.generated.resources.item_type
import terminalexecutor.composeapp.generated.resources.name
import terminalexecutor.composeapp.generated.resources.ok

@Composable
fun AddItemScreen(
    modifier: Modifier = Modifier,
    randomIdGenerator: RandomIdGenerator,
    groupOnly: Boolean,
    onCancel: () -> Unit,
    onAddItem: (ListItemUiState) -> Unit,
    onAddGroup: (ListItemGroupUiState) -> Unit
) {
    Column(modifier = Modifier.background(MaterialTheme.colors.surface).padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        val radioOptions = listOf(stringResource(Res.string.group), stringResource(Res.string.command))
        val (selectedOption, onOptionSelected) = remember { mutableStateOf(radioOptions[0]) }
        val addingCommand = radioOptions.indexOf(selectedOption) == 1
        var nameText by remember { mutableStateOf("") }
        var commandText by remember { mutableStateOf("") }

        val confirmEnabled = if (!addingCommand) {
            nameText.trim().isNotEmpty()
        } else {
            nameText.trim().isNotEmpty() && commandText.trim().isNotEmpty()
        }


        if (!groupOnly) {
            Text(stringResource(Res.string.item_type), color = MaterialTheme.colors.onSurface)
            Spacer(modifier = Modifier.height(4.dp))
            Row(modifier.selectableGroup().thinOutline()) {
                radioOptions.forEach { text ->
                    Row(
                        Modifier
                            .height(56.dp)
                            .width(160.dp)
                            .selectable(
                                selected = (text == selectedOption),
                                onClick = { onOptionSelected(text) },
                                role = Role.RadioButton
                            )
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (text == selectedOption),
                            onClick = null
                        )
                        Text(
                            text = text,
                            modifier = Modifier.padding(start = 16.dp),
                            color = MaterialTheme.colors.onSurface
                        )
                    }
                }
            }
        } else {
            Text(
                stringResource(Res.string.enter_group_name),
                color = MaterialTheme.colors.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
        }

        Spacer(modifier.height(8.dp))


        TextField(
            value = nameText,
            onValueChange = { nameText = it },
            modifier = Modifier.width(800.dp).thinOutline(),
            readOnly = false,
            label = { Text(stringResource(Res.string.name)) },
            singleLine = true,
            colors = TextFieldDefaults.textFieldColors(textColor = MaterialTheme.colors.onSurface)
        )

        Spacer(modifier.height(8.dp))


        if (addingCommand) {
            TextField(
                value = commandText,
                onValueChange = { commandText = it },
                modifier = Modifier.width(800.dp).thinOutline(),
                readOnly = false,
                label = { Text(stringResource(Res.string.command)) },
                colors = TextFieldDefaults.textFieldColors(textColor = MaterialTheme.colors.onSurface),
            )

            Spacer(modifier.height(8.dp))
        }

        Row {
            Spacer(modifier = Modifier.weight(1.0f))
            Button(onClick = onCancel) {
                Text(stringResource(Res.string.cancel))
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    if (addingCommand) {
                        onAddItem(ListItemUiState(nameText, commandText.split(" ")))
                    } else {
                        onAddGroup(ListItemGroupUiState(randomIdGenerator.generateId(), nameText, emptyList(), emptyList()))
                    }
                },
                enabled = confirmEnabled
            ) {
                Text(stringResource(Res.string.ok))
            }
        }
    }
}