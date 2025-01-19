package com.bojan.terminalexecutor.ktx

fun Array<String>.replaceParams(paramsList: String): Array<String> {
    val params = paramsList.split(",").map { it.trim() }
    return map { command ->
        command.replace(Regex("\\$\\d+")) { matchResult ->
            val index = matchResult.value.substring(1).toIntOrNull()
            if (index != null && index <= params.size) {
                params[index - 1] // Adjust for 0-based index
            } else {
                ""
            }
        }
    }.toTypedArray()
}