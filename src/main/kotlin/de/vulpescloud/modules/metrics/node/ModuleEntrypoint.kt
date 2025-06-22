package de.vulpescloud.modules.metrics.node

import com.influxdb.client.InfluxDBClientOptions
import de.vulpescloud.api.module.VulpesModule
import de.vulpescloud.api.virtualconfig.VirtualConfig
import de.vulpescloud.api.virtualconfig.VirtualConfigProvider
import de.vulpescloud.modules.metrics.common.MetricsUtil

@Suppress("unused")
class ModuleEntrypoint : VulpesModule {

    private lateinit var nodeStatsGetter: NodeStatsGetter
    private lateinit var config: VirtualConfig

    override fun onDisable() {
        nodeStatsGetter.cancel()
    }

    override fun onEnable() {
        config = VirtualConfigProvider.getConfig("Metrics-Module-Global")
        initConfig()

        MetricsUtil.initialize(config)

        nodeStatsGetter = NodeStatsGetter()
        nodeStatsGetter.run()
    }

    fun initConfig() {
        config.getEntry("influxdb.bucket", "VulpesCloudMetrics")
        config.getEntry("influxdb.organization", "-")
        config.getEntry("influxdb.interval", 10L)
        config.getEntry("influxdb.url", "http://localhost:8086")
        config.getEntry("influxdb.username", "influxdb")
        config.getEntry("influxdb.password", "influxdb")
        config.getEntry("influxdb.token", "token")
        config.getEntry("influxdb.authScheme", InfluxDBClientOptions.AuthScheme.SESSION.toString())

        config.publish()
    }
}
