package com.bojan.terminalexecutor.settings

import com.bojan.terminalexecutor.utils.getWorkingDirectory
import java.io.File
import java.util.Properties

class TerminalExecutorSettings {
    private val properties = Properties()

    fun putString(key: String, value: String) {
        properties[key] = value
        saveToFile()
    }

    fun putInt(key: String, value: Int) {
        properties[key] = value.toString()
        saveToFile()
    }

    fun putBoolean(key: String, value: Boolean) {
        properties[key] = value.toString()
        saveToFile()
    }

    fun putMapItem(key: String, pair: Pair<String, String>) {
        val oldMap = getMap(key)
        oldMap[pair.first] = pair.second
        properties[key] = oldMap.entries.joinToString(MAP_STRING_SEPARATOR)
        saveToFile()
    }

    fun getMap(key: String): MutableMap<String, String> {
        val value = properties.getProperty(key)
        if (value != null) {
            val map = value.split(MAP_STRING_SEPARATOR).associate {
                val (left, right) = it.split("=")
                left to right
            }
            return map.toMutableMap()
        } else {
            return mutableMapOf()
        }
    }

    fun getString(key: String): String? = properties.getProperty(key)

    fun getBoolean(key: String): Boolean? = properties.getProperty(key)?.toBooleanStrictOrNull()

    fun getInt(key: String): Int? = properties.getProperty(key)?.toIntOrNull()

    private fun saveToFile() {
        val settingsFile = getSettingsFile()
        settingsFile.parentFile.mkdirs()
        settingsFile.outputStream().use { outputStream ->
            properties.store(outputStream, null)
        }
    }

    fun loadSettings() {
        val settingsFile = getSettingsFile()
        if (settingsFile.exists()) {
            settingsFile.inputStream().use { input ->
                properties.load(input)
            }
        }
    }

    private fun getSettingsFile() = File(getWorkingDirectory(), SETTINGS_FILE_NAME)

    companion object {
        const val SETTINGS_FILE_NAME = "TeSettings.tes"
        const val MAP_STRING_SEPARATOR = ","
    }
}