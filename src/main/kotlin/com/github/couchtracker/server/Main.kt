package com.github.couchtracker.server

import com.github.couchtracker.server.routes.showRoutes
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.resources.*
import io.ktor.server.routing.*
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("Main")

fun main() {
    val config = Config.load()
    logger.info("Detected configuration: {}", config)
    embeddedServer(
        Netty,
        port = config.port,
        host = config.host,
        module = { couchTrackerModule(config) }
    ).start(wait = true)
}

fun Application.couchTrackerModule(config: Config) {
    install(CallLogging)
    install(ContentNegotiation) { json() }
    install(RequestValidation)
    install(StatusPages) {
        exception<IgnoreException> { _, _ -> }
    }
    install(Resources)

    val applicationData = runBlocking { ApplicationData.create(this@couchTrackerModule, config) }

    routing {
        showRoutes(applicationData)
    }
}

class IgnoreException : RuntimeException()