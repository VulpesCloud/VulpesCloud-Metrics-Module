plugins {
    kotlin("jvm") version "2.1.10"
}

group = "de.vulpescloud"
version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://repo.vulpescloud.de/snapshots/")
}

dependencies {
    compileOnly("de.vulpescloud:VulpesCloud-api:2.0.0-ALPHA")
    compileOnly("de.vulpescloud:VulpesCloud-node:2.0.0-ALPHA")
    compileOnly("de.vulpescloud:VulpesCloud-bridge:2.0.0-ALPHA")
    compileOnly("de.vulpescloud:JedisWrapper:1.1.1")
    compileOnly("org.jetbrains.exposed:exposed-core:0.61.0")
    compileOnly("io.insert-koin:koin-core:4.0.3")
    // implementation("com.influxdb:influxdb-client-java:7.2.0")
}

kotlin {
    jvmToolchain(21)
}