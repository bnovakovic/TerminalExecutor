package com.bojan.terminalexecutor.commandexecutor

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

suspend fun executeCommand(command: Array<String>, workingDir: File): CommandResult {
    return try {
        withContext(Dispatchers.IO) {
            val processBuilder = ProcessBuilder(*command)
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