package de.vulpescloud.modules.metrics.velocity

import com.influxdb.client.write.Point
import com.sun.management.OperatingSystemMXBean
import com.velocitypowered.api.proxy.ProxyServer
import de.vulpescloud.modules.metrics.common.MetricsUtil
import java.lang.management.ManagementFactory
import java.util.concurrent.TimeUnit

class ProxyStatsGetter(proxyServer: ProxyServer, velocityEntrypoint: VelocityEntrypoint) {

    init {
        proxyServer.scheduler
            .buildTask(
                velocityEntrypoint,
                Runnable {
                    val runtime = Runtime.getRuntime()
                    val point = Point("service.stats")
                    point.apply {
                        addTag("service", "proxy")
                        addField("onlinePlayers", proxyServer.allPlayers.size)
                        addField("mspt", "N/A")
                        addField("tps", "N/A")
                        addField("totalMemory", runtime.totalMemory())
                        addField("freeMemory", runtime.freeMemory())
                        addField("usedMemory", runtime.totalMemory() - runtime.freeMemory())
                        addField(
                            "cpuLoad",
                            (ManagementFactory.getOperatingSystemMXBean() as OperatingSystemMXBean)
                                .processCpuLoad * 100,
                        )
                    }


                    MetricsUtil.writePoint(point)
                },
            )
            .repeat(15, TimeUnit.SECONDS)
            .schedule()
    }
}
