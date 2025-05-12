package de.vulpescloud.modules.metrics.common

import org.jetbrains.exposed.sql.Table

object MetricsTable : Table("vulpescloud-metrics") {
    val id = integer("id").autoIncrement()
    val name = text("name")
    val value = text("value")
    val timestamp = long("timestamp")

    override val primaryKey = PrimaryKey(id)
}
