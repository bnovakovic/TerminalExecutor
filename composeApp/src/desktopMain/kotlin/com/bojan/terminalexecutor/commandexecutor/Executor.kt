package com.bojan.terminalexecutor.commandexecutor

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

suspend fun executeCommand(command: Array<String>, workingDir: File, appPaths: Map<String, String>): CommandResult {

    if (command.isEmpty()) {
        return CommandResult(null, "Error: empty command")
    }
    return try {
        withContext(Dispatchers.IO) {

            val mutable = command.toMutableList()
            val key = mutable.first()
            val found = appPaths[key]
            found?.let { foundPath ->
                mutable.set(0, foundPath)
            }

            val processBuilder = ProcessBuilder(mutable.toList())
            processBuilder.redirectErrorStream(true)
            processBuilder.directory(workingDir)
            val process = processBuilder.start()

            val output = process.inputStream.bufferedReader().use { it.readText() }
            process.waitFor()

            if (process.exitValue() == 0) {
                CommandResult(output, null)
            } else {
                CommandResult(null, "Error: Exit Value: ${process.exitValue()}\n. $output")
            }
        }
    } catch (e: Exception) {
        CommandResult(null, e.message)
    }
}