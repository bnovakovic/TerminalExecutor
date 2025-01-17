package com.bojan.terminalexecutor

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.bojan.terminalexecutor.commandexecutor.executeCommand
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    println("Started the app!")
    MaterialTheme {
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Button(onClick = {
                val commandList = mutableListOf("adb", "devices")
                executeCommand(commandList.toTypedArray())
                    .onSuccess { output ->
                        println("Success: $output")
                    }
                    .onFailure { error ->
                        println("Failure: $error ")
                    }

            }) {
                Text("Click me!")
            }
        }
    }
}