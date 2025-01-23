package com.bojan.terminalexecutor.ktx

fun List<String>.replaceParams(paramsList: String): List<String> {
    val params = paramsList.split(",").map { it.trim() }
    return map { command ->
        command.replace(Regex("\\$\\d+")) { matchResult ->
            val index = matchResult.value.substring(1).toIntOrNull()
            if (index != null && index <= params.size) {
                params[index - 1]
            } else {
                ""
            }
        }
    }
}