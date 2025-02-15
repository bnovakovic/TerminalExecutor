package com.bojan.terminalexecutor.seriazible

import kotlinx.serialization.Serializable

@Serializable
data class ParamInfo(val name: String, val value: String)