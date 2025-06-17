plugins {
    kotlin("jvm") version "2.1.10"
    id("com.gradleup.shadow") version "9.0.0-beta13"
    id("maven-publish")
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
    compileOnly("org.jetbrains.exposed:exposed-core:1.0.0-beta-2")
    compileOnly("org.jetbrains.exposed:exposed-jdbc:1.0.0-beta-2")
    compileOnly("io.insert-koin:koin-core:4.0.3")
    implementation("com.influxdb:influxdb-client-java:7.3.0")
}

kotlin { jvmToolchain(21) }

publishing {
    repositories {
        maven {
            name = "vulpescloudReleases"
            url = uri("https://repo.vulpescloud.de/releases/")
            credentials {
                username = System.getenv("REPO_USERNAME")
                password = System.getenv("REPO_PASSWORD")
            }
        }
    }

    publications {
        create<MavenPublication>("maven") {
            groupId = rootProject.group.toString()
            artifactId = project.name
            version = rootProject.version.toString()
            from(project.components["java"])
        }
    }
}

java {
    withSourcesJar()
    withJavadocJar()
}
