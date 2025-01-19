package com.bojan.terminalexecutor.utils

class RandomIdGenerator {
    private val storedIds = mutableSetOf<String>()

    fun generateId(): String {
        val generated = generateStringId()
        val added = storedIds.add(generated)
        if (!added) {
            println("$generated already exist. Have to try again")
            return generateId()
        }
        return generated
    }

    private fun generateStringId() : String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..RANDOM_ID_COMPLEXITY)
            .map { allowedChars.random() }
            .joinToString("")
    }

    fun printStoredIds() {
        println("Generated ID's:")
        storedIds.forEach {
            println(it)
        }
    }

    companion object {
        const val RANDOM_ID_COMPLEXITY = 100
    }
}