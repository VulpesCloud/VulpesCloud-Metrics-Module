package de.vulpescloud.modules.metrics.node

import com.sun.management.OperatingSystemMXBean
import de.vulpescloud.node.Scheduler
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
            val totalPhysicalMemorySize = osMXBean.totalMemorySize
            val freePhysicalMemorySize = osMXBean.freeMemorySize
            val totalSwapSpaceSize = osMXBean.totalSwapSpaceSize
            val freeSwapSpaceSize = osMXBean.freeSwapSpaceSize
            val cpuLoad = osMXBean.cpuLoad
            val processCpuLoad = osMXBean.processCpuLoad
            val processCpuTime = osMXBean.processCpuTime
            val arch = osMXBean.arch
            val name = osMXBean.name
            val version = osMXBean.version
            val objectName = osMXBean.objectName

            logger.info("System Load Average: $systemLoadAverage")
            logger.info("Available Processors: $availableProcessors")
            logger.info("Total Physical Memory Size: $totalPhysicalMemorySize")
            logger.info("Free Physical Memory Size: $freePhysicalMemorySize")
            logger.info("Total Swap Space Size: $totalSwapSpaceSize")
            logger.info("Free Swap Space Size: $freeSwapSpaceSize")
            logger.info("CPU Load: $cpuLoad")
            logger.info("Process CPU Load: $processCpuLoad")
            logger.info("Process CPU Time: $processCpuTime")
            logger.info("Arch: $arch")
            logger.info("Name: $name")
            logger.info("Version: $version")
            logger.info("Object Name: $objectName")

            delay(5000)
        }
    }
}
