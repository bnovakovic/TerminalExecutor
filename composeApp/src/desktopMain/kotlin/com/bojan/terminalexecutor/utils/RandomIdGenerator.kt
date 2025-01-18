package com.bojan.terminalexecutor.utils

class RandomIdGenerator {
    private val storedIds = mutableSetOf<String>()

    fun generateId(): String {
        val generated = generateStringId(RANDOM_ID_COMPLEXITY)
        val added = storedIds.add(generated)
        println("Success: $added, ID: $generated")
        return generated
    }

    private fun generateStringId(length: Int) : String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..length)
            .map { allowedChars.random() }
            .joinToString("")
    }

    companion object {
        const val RANDOM_ID_COMPLEXITY = 100
    }
}