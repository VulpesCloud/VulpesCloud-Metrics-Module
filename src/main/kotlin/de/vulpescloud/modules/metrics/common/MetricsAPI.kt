package de.vulpescloud.modules.metrics.common

import com.influxdb.query.FluxRecord
import com.influxdb.query.FluxTable
import de.vulpescloud.api.virtualconfig.VirtualConfigProvider
import de.vulpescloud.modules.metrics.common.models.NodeStatsModel
import de.vulpescloud.modules.metrics.common.models.ServiceStatsModel
import java.time.Instant

@Suppress("unused")
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

    /**
     * Gibt die NodeStats der letzten 24 Stunden zurück
     *
     * @param nodeName Optional: Name des spezifischen Nodes
     * @return Liste von NodeStatsModel für die letzten 24 Stunden
     */
    fun getLast24hNodeStats(nodeName: String? = null): List<NodeStatsModel> {
        val nodeFilter = nodeName?.let { """ |> filter(fn: (r) => r["node"] == "$it")""" } ?: ""
        val flux =
            """
        from(bucket: "$bucket")
            |> range(start: -24h)
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
            timestamp = records.first().time?.toEpochMilli()
        )
    }

    /**
     * Erstellt ein ServiceStatsModel aus den InfluxDB-Records
     */
    private fun createServiceStatsFromRecords(records: List<FluxRecord>): ServiceStatsModel {
        fun getValue(field: String) =
            records.first { it.getValueByKey("_field") == field }.getValueByKey("_value")

        return ServiceStatsModel(
            onlinePlayers = (getValue("onlinePlayers") as Number).toInt(),
            mspt = if (getValue("mspt") == "N/A") 0.0 else (getValue("mspt") as Number).toDouble(),
            tps = if (getValue("tps") == "N/A") 0.0 else (getValue("tps") as Number).toDouble(),
            totalMemory = (getValue("totalMemory") as Number).toLong(),
            freeMemory = (getValue("freeMemory") as Number).toLong(),
            usedMemory = (getValue("usedMemory") as Number).toLong(),
            cpuLoad = (getValue("cpuLoad") as Number).toDouble(),
            timestamp = records.first().time?.toEpochMilli()
        )
    }

    /**
     * Gibt die ServiceStats für einen bestimmten Zeitraum zurück
     *
     * @param from Startzeitpunkt
     * @param to Endzeitpunkt
     * @param serviceName Optional: Name des spezifischen Services
     * @return Liste von ServiceStatsModel für den angegebenen Zeitraum
     */
    fun getServiceStatsForTimespan(
        from: Instant,
        to: Instant,
        serviceName: String? = null,
    ): List<ServiceStatsModel> {
        val serviceFilter = serviceName?.let { """ |> filter(fn: (r) => r["service"] == "$it")""" } ?: ""
        val flux = """
        from(bucket: "$bucket")
            |> range(start: $from, stop: $to)
            |> filter(fn: (r) => r["_measurement"] == "service.stats")
            $serviceFilter
    """.trimIndent()

        val queryApi = MetricsUtil.influxDBClient.queryApi
        val tables: List<FluxTable> = queryApi.query(flux)

        return tables
            .flatMap { it.records }
            .groupBy { it.time }
            .map { (_, records) -> createServiceStatsFromRecords(records) }
    }

    /**
     * Gibt die ServiceStats der letzten 24 Stunden zurück
     *
     * @param serviceName Optional: Name des spezifischen Services
     * @return Liste von ServiceStatsModel für die letzten 24 Stunden
     */
    fun getLast24hServiceStats(serviceName: String? = null): List<ServiceStatsModel> {
        val serviceFilter = serviceName?.let { """ |> filter(fn: (r) => r["service"] == "$it")""" } ?: ""
        val flux = """
        from(bucket: "$bucket")
            |> range(start: -24h)
            |> filter(fn: (r) => r["_measurement"] == "service.stats")
            $serviceFilter
    """.trimIndent()

        val queryApi = MetricsUtil.influxDBClient.queryApi
        val tables: List<FluxTable> = queryApi.query(flux)

        return tables
            .flatMap { it.records }
            .groupBy { it.time }
            .map { (_, records) -> createServiceStatsFromRecords(records) }
    }

    /**
     * Gibt die aktuellsten ServiceStats zurück
     *
     * @param serviceName Optional: Name des spezifischen Services
     * @return Das neueste ServiceStatsModel oder null wenn keine Daten verfügbar sind
     */
    fun getLatestServiceStats(serviceName: String? = null): ServiceStatsModel? {
        val serviceFilter = serviceName?.let { """ |> filter(fn: (r) => r["service"] == "$it")""" } ?: ""
        val flux = """
        from(bucket: "$bucket")
            |> range(start: -1m)
            |> filter(fn: (r) => r["_measurement"] == "service.stats")
            $serviceFilter
            |> last()
    """.trimIndent()

        val queryApi = MetricsUtil.influxDBClient.queryApi
        val tables: List<FluxTable> = queryApi.query(flux)

        return tables
            .flatMap { it.records }
            .groupBy { it.time }
            .map { (_, records) -> createServiceStatsFromRecords(records) }
            .firstOrNull()
    }
}