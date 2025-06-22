package de.vulpescloud.modules.metrics.velocity

import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.plugin.Plugin
import com.velocitypowered.api.proxy.ProxyServer
import de.vulpescloud.api.virtualconfig.VirtualConfig
import de.vulpescloud.api.virtualconfig.VirtualConfigProvider
import de.vulpescloud.modules.metrics.common.MetricsUtil
import jakarta.inject.Inject

@Suppress("unused")
@Plugin(id = "vulpescloud-metrics-module", name = "VulpesCloud-Metrics-Module", authors = ["TheCGuy"])
class VelocityEntrypoint @Inject
constructor(
    private val proxyServer: ProxyServer,
) {
    private lateinit var config: VirtualConfig

    @Subscribe
    fun onProxyInitializeEvent(event: ProxyInitializeEvent) {
        config = VirtualConfigProvider.getConfig("Metrics-Module-Global")
        MetricsUtil.initialize(config)

        ProxyStatsGetter(proxyServer, this)
    }
}
