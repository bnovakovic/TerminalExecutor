package com.bojan.terminalexecutor.utils

import java.io.File
import java.nio.file.FileSystems

fun getWorkingDirectory(): String = "${getOsAppDataDirectory()}${getFileSeparator()}TerminalExecutor"
private fun getOsAppDataDirectory(): File {
    return when (getOs()) {
        JvmOs.WINDOWS -> File(System.getenv("AppData"))
        JvmOs.LINUX -> File(getUserHome(),".config")
        JvmOs.MAC -> File(getUserHome(), "/Library/Application Support/")
    }
}

fun getUserHome() = File(System.getProperty("user.home"))

fun getOsName(): String = System.getProperty("os.name")

fun getOs(): JvmOs {
    val osName = getOsName().lowercase()
    if (osName.contains("windows")) return JvmOs.WINDOWS
    if (osName.contains("mac")) return JvmOs.MAC
    if (osName.contains("nux")) return JvmOs.LINUX
    throw IllegalArgumentException("Operating system $osName not supported.")
}

enum class JvmOs { WINDOWS, LINUX, MAC }

fun getFileSeparator(): String = FileSystems.getDefault().separator

fun getCurrentDir() = File(System.getProperty("user.dir") ?: "")