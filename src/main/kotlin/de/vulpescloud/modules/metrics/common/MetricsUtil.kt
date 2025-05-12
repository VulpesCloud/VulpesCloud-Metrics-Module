package de.vulpescloud.modules.metrics.common

import org.jetbrains.exposed.sql.insertIgnore
import org.jetbrains.exposed.sql.transactions.transaction

object MetricsUtil {

    fun setValue(name: String, value: String) {
        transaction {
            MetricsTable.insertIgnore {
                it[MetricsTable.name] = name
                it[MetricsTable.value] = value
                it[timestamp] = System.currentTimeMillis()
            }
        }
    }
}
