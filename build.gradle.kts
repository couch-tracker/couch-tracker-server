import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import io.gitlab.arturbosch.detekt.Detekt
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.8.10"
    kotlin("plugin.serialization") version "1.8.10"
    id("io.gitlab.arturbosch.detekt").version("1.22.0")
    id("io.ktor.plugin") version "2.2.3"
    id("com.github.ben-manes.versions") version "0.45.0"
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
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.22.0")

    // Utilities
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")

    // Ktor
    val ktorVersion = "2.2.3"
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-server-call-logging:$ktorVersion")
    implementation("io.ktor:ktor-server-compression:$ktorVersion")
    implementation("io.ktor:ktor-server-request-validation:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
    implementation("io.ktor:ktor-server-resources:$ktorVersion")
    implementation("io.ktor:ktor-server-status-pages:$ktorVersion")
    implementation("io.ktor:ktor-server-auth:$ktorVersion")
    implementation("io.ktor:ktor-server-auth-jwt:$ktorVersion")

    // Logs
    implementation("ch.qos.logback:logback-classic:1.4.5")
    implementation("io.github.microutils:kotlin-logging-jvm:3.0.5")

    // Mongo
    implementation("org.litote.kmongo:kmongo-coroutine-serialization:4.8.0")
    implementation("org.litote.kmongo:kmongo-id-serialization:4.8.0")

    // External clients
    implementation("com.uwetrottmann.tmdb2:tmdb-java:2.8.1")

    // Configuration parsing
    val hopliteVersion = "2.7.1"
    implementation("com.sksamuel.hoplite:hoplite-core:$hopliteVersion")
    implementation("com.sksamuel.hoplite:hoplite-toml:$hopliteVersion")

    // Other
    implementation("de.mkammerer:argon2-jvm:2.11")

    // TEST DEPENDENCIES
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.2")
    testImplementation(kotlin("test"))
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

tasks.withType<Detekt>().configureEach {
    reports {
        html.required.set(true)
        md.required.set(true)
    }
}

tasks.buildFatJar {
    dependsOn += "detektMain"
}

detekt {
    toolVersion = "1.22.0"
    config = files("detekt.yml")
    buildUponDefaultConfig = true
}

tasks.withType<DependencyUpdatesTask> {
    fun isStable(version: String): Boolean {
        val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { version.toUpperCase().contains(it) }
        val regex = "^[0-9,.v-]+(-r)?$".toRegex()
        return stableKeyword || regex.matches(version)
    }
    gradleReleaseChannel = "current"
    rejectVersionIf {
        isStable(candidate.version).not()
    }
}
