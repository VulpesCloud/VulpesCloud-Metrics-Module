package de.vulpescloud.modules.metrics.common.models

data class NodeStatsModel(
    val systemLoadAverage: Double,
    val availableProcessors: Int,
    val totalSystemMemory: Long,
    val freeSystemMemory: Long,
    val usedSystemMemory: Long,
    val totalSwapSpace: Long,
    val freeSwapSpace: Long,
    val usedSwapSpace: Long,
    val cpuLoad: Double,
    val processCpuLoad: Double,
    val processCpuTime: Long,
    val arch: String,
    val name: String,
    val version: String,
    val timestamp: Long?,
)
