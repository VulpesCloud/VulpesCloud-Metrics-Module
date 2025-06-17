package de.vulpescloud.modules.metrics.node

import com.influxdb.client.write.Point
import com.sun.management.OperatingSystemMXBean
import de.vulpescloud.modules.metrics.common.MetricsUtil
import de.vulpescloud.node.Scheduler
import de.vulpescloud.node.VulpesNode.clusterProvider
import java.lang.management.ManagementFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory

class NodeStatsGetter : Scheduler() {
    private val osMXBean = ManagementFactory.getOperatingSystemMXBean() as OperatingSystemMXBean

    private val logger = LoggerFactory.getLogger("TempNodeStatsGetterLoggerVeryNotCoolNoJokeniug")

    override fun run() = launch {
        while (true) {
            val systemLoadAverage = osMXBean.systemLoadAverage
            val availableProcessors = osMXBean.availableProcessors
            val totalSystemMemory = osMXBean.totalMemorySize / (1024 * 1024)
            val freeSystemMemory = osMXBean.freeMemorySize / (1024 * 1024)
            val usedSystemMemory = totalSystemMemory - freeSystemMemory

            val totalSwapSpace = osMXBean.totalSwapSpaceSize / (1024 * 1024)
            val freeSwapSpace = osMXBean.freeSwapSpaceSize / (1024 * 1024)
            val usedSwapSpace = totalSwapSpace - freeSwapSpace

            val cpuLoad = osMXBean.cpuLoad
            val processCpuLoad = osMXBean.processCpuLoad
            val processCpuTime = osMXBean.processCpuTime

            val arch = osMXBean.arch
            val name = osMXBean.name
            val version = osMXBean.version

            logger.info("")
            logger.info("Writing New Point with data:")
            logger.info("")


            val point = Point("node.systemStats")
            point.addTag("node", clusterProvider.localNode().name)
            point.apply {
                addField("systemLoadAverage", systemLoadAverage)
                addField("availableProcessors", availableProcessors)

                addField("totalSystemMemory", totalSystemMemory)
                addField("freeSystemMemory", freeSystemMemory)
                addField("usedSystemMemory", usedSystemMemory)

                addField("totalSwapSpace", totalSwapSpace)
                addField("freeSwapSpace", freeSwapSpace)
                addField("usedSwapSpace", usedSwapSpace)

                addField("cpuLoad", cpuLoad)
                addField("processCpuLoad", processCpuLoad)
                addField("processCpuTime", processCpuTime)

                addField("arch", arch)
                addField("name", name)
                addField("version", version)
            }

            MetricsUtil.writePoint(point)

            delay(15000)
        }
    }
}
