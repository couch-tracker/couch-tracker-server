import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.20"
    kotlin("plugin.serialization") version "1.7.20"
    id("io.ktor.plugin") version "2.1.3"
    application
}

group = "com.github.couch-tracker.server"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}
kotlin {
    jvmToolchain(17)
}

dependencies {
    testImplementation(kotlin("test"))

    // Utilities
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")

    // Ktor
    val ktorVersion = "2.1.3"
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-server-call-logging:$ktorVersion")
    implementation("io.ktor:ktor-server-request-validation:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
    implementation("io.ktor:ktor-server-resources:$ktorVersion")
    implementation("io.ktor:ktor-server-status-pages:$ktorVersion")

    // Logs
    implementation("ch.qos.logback:logback-classic:1.4.5")

    // Mongo
    implementation("org.litote.kmongo:kmongo-coroutine-serialization:4.8.0")
    implementation("org.litote.kmongo:kmongo-id-serialization:4.8.0")

    // External clients
    implementation("com.uwetrottmann.tmdb2:tmdb-java:2.8.1")

    // Configuration parsing
    implementation("com.sksamuel.hoplite:hoplite-core:2.7.0")
    implementation("com.sksamuel.hoplite:hoplite-toml:2.7.0")


    // TEST DEPENDENCIES
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.1")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}

application {
    mainClass.set("com.github.couchtracker.server.MainKt")
}

ktor {
    fatJar {
        archiveFileName.set("couch-tracker-server.jar")
    }
}

