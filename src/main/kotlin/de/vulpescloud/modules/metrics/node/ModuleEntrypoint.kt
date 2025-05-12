package de.vulpescloud.modules.metrics.node

import de.vulpescloud.api.module.VulpesModule

class ModuleEntrypoint : VulpesModule {

    private lateinit var nodeStatsGetter: NodeStatsGetter

    override fun onDisable() {
        nodeStatsGetter.cancel()
    }

    override fun onEnable() {
        nodeStatsGetter = NodeStatsGetter()

        nodeStatsGetter.run()
    }
}
