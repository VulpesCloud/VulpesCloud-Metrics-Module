package de.vulpescloud.modules.metrics.common.models

data class ServiceStatsModel(
    val onlinePlayers: Int,
    val mspt: Double,
    val tps: Double,
    val totalMemory: Long,
    val freeMemory: Long,
    val usedMemory: Long,
    val cpuLoad: Double,
    val timestamp: Long?
)
