package com.bojan.terminalexecutor.commandexecutor

fun executeCommand(command: Array<String>): CommandResult {
    return try {
        val processBuilder = ProcessBuilder(*command)
        processBuilder.redirectErrorStream(true)
        val process = processBuilder.start()

        val output = process.inputStream.bufferedReader().use { it.readText() }
        process.waitFor()

        if (process.exitValue() == 0) {
            CommandResult(output, null)
        } else {
            CommandResult(null, "Command failed with exit code: ${process.exitValue()}")
        }
    } catch (e: Exception) {
        CommandResult(null, "Error executing command: ${e.message}")
    }
}