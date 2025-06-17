package de.vulpescloud.modules.metrics.common

import com.influxdb.client.InfluxDBClient
import com.influxdb.client.InfluxDBClientFactory
import com.influxdb.client.InfluxDBClientOptions
import com.influxdb.client.WriteApi
import com.influxdb.client.domain.WritePrecision
import com.influxdb.client.write.Point
import de.vulpescloud.api.virtualconfig.VirtualConfig

object MetricsUtil {

    lateinit var influxDBClient: InfluxDBClient
    private lateinit var influxDBWriteAPI: WriteApi

    fun writePoint(point: Point) {
        point.time(System.currentTimeMillis(), WritePrecision.MS)
        influxDBWriteAPI.writePoint(point)
    }

    fun initialize(config: VirtualConfig) {
        influxDBClient =
            InfluxDBClientFactory.create(
                InfluxDBClientOptions.builder()
                    .url(config.getEntry("influxdb.url", "http://localhost:8086"))
                    .bucket(config.getEntry("influxdb.bucket", "VulpesCloudMetrics"))
                    .org(config.getEntry("influxdb.organization", "-"))
                    .apply {
                        when (
                            InfluxDBClientOptions.AuthScheme.valueOf(
                                config.getEntry(
                                    "influxdb.authScheme",
                                    InfluxDBClientOptions.AuthScheme.SESSION.toString(),
                                )
                            )
                        ) {
                            InfluxDBClientOptions.AuthScheme.SESSION ->
                                authenticate(
                                    config.getEntry("influxdb.username", "influxdb"),
                                    config.getEntry("influxdb.password", "influxdb").toCharArray(),
                                )
                            InfluxDBClientOptions.AuthScheme.TOKEN ->
                                authenticateToken(
                                    config.getEntry("influxdb.token", "-").toCharArray()
                                )
                        }
                    }
                    .build()
            )
        influxDBWriteAPI = influxDBClient.makeWriteApi()
    }
}
