package com.bojan.terminalexecutor.commandexecutor

class CommandResult(private val output: String?, private val error: String?) {
    fun onSuccess(action: (String) -> Unit): CommandResult {
        output?.let { action(it) }
        return this
    }

    fun onFailure(action: (String) -> Unit): CommandResult {
        error?.let { action(it) }
        return this
    }
}