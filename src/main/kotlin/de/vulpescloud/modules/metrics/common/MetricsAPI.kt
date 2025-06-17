package de.vulpescloud.modules.metrics.common

import com.influxdb.query.FluxRecord
import com.influxdb.query.FluxTable
import de.vulpescloud.api.virtualconfig.VirtualConfigProvider
import de.vulpescloud.modules.metrics.common.models.NodeStatsModel
import java.time.Instant

object MetricsAPI {

    private val config = VirtualConfigProvider.getConfig("Metrics-Module-Global")
    private val bucket = config.getEntry("influxdb.bucket", "VulpesCloudMetrics")

    /**
     * Gibt die NodeStats für einen bestimmten Zeitraum zurück
     *
     * @param from Startzeitpunkt
     * @param to Endzeitpunkt
     * @param nodeName Optional: Name des spezifischen Nodes
     * @return Liste von NodeStatsModel für den angegebenen Zeitraum
     */
    fun getNodeStatsForTimespan(
        from: Instant,
        to: Instant,
        nodeName: String? = null,
    ): List<NodeStatsModel> {
        val nodeFilter = nodeName?.let { """ |> filter(fn: (r) => r["node"] == "$it")""" } ?: ""
        val flux =
            """
            from(bucket: "$bucket")
                |> range(start: $from, stop: $to)
                |> filter(fn: (r) => r["_measurement"] == "node.systemStats")
                $nodeFilter
        """
                .trimIndent()

        val queryApi = MetricsUtil.influxDBClient.queryApi
        val tables: List<FluxTable> = queryApi.query(flux)

        return tables
            .flatMap { it.records }
            .groupBy { it.time }
            .map { (_, records) -> createNodeStatsFromRecords(records) }
    }

    /**
     * Gibt die aktuellsten NodeStats zurück
     *
     * @param nodeName Optional: Name des spezifischen Nodes
     * @return Das neueste NodeStatsModel oder null wenn keine Daten verfügbar sind
     */
    fun getLatestNodeStats(nodeName: String? = null): NodeStatsModel? {
        val nodeFilter = nodeName?.let { """ |> filter(fn: (r) => r["node"] == "$it")""" } ?: ""
        val flux =
            """
            from(bucket: "$bucket")
                |> range(start: -1m)
                |> filter(fn: (r) => r["_measurement"] == "node.systemStats")
                $nodeFilter
                |> last()
        """
                .trimIndent()

        val queryApi = MetricsUtil.influxDBClient.queryApi
        val tables: List<FluxTable> = queryApi.query(flux)

        return tables
            .flatMap { it.records }
            .groupBy { it.time }
            .map { (_, records) -> createNodeStatsFromRecords(records) }
            .firstOrNull()
    }

    private fun createNodeStatsFromRecords(records: List<FluxRecord>): NodeStatsModel {
        fun getValue(field: String) =
            records.first { it.getValueByKey("_field") == field }.getValueByKey("_value")

        return NodeStatsModel(
            systemLoadAverage = (getValue("systemLoadAverage") as Number).toDouble(),
            availableProcessors = (getValue("availableProcessors") as Number).toInt(),
            totalSystemMemory = (getValue("totalSystemMemory") as Number).toLong(),
            freeSystemMemory = (getValue("freeSystemMemory") as Number).toLong(),
            usedSystemMemory = (getValue("usedSystemMemory") as Number).toLong(),
            totalSwapSpace = (getValue("totalSwapSpace") as Number).toLong(),
            freeSwapSpace = (getValue("freeSwapSpace") as Number).toLong(),
            usedSwapSpace = (getValue("usedSwapSpace") as Number).toLong(),
            cpuLoad = (getValue("cpuLoad") as Number).toDouble(),
            processCpuLoad = (getValue("processCpuLoad") as Number).toDouble(),
            processCpuTime = (getValue("processCpuTime") as Number).toLong(),
            arch = getValue("arch") as String,
            name = getValue("name") as String,
            version = getValue("version") as String,
        )
    }
}
