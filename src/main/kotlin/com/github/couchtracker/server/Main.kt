package com.github.couchtracker.server

import com.github.couchtracker.server.config.Config
import com.github.couchtracker.server.routes.authRoutes
import com.github.couchtracker.server.routes.showRoutes
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.resources.*
import io.ktor.server.routing.*
import kotlinx.coroutines.runBlocking
import ch.qos.logback.classic.Logger
import io.ktor.server.response.*
import org.slf4j.LoggerFactory


fun main() {
    val config = Config.load()
    // TODO log level is set too late, so some logs are always printed no matter the logLevel
    (LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME) as Logger).apply {
        level = config.logLevel.level
    }

    embeddedServer(
        Netty,
        port = config.port,
        host = config.host,
        module = { couchTrackerModule(config) }
    ).start(wait = true)
}

fun Application.couchTrackerModule(config: Config) {
    val applicationData = runBlocking { ApplicationData.create(this@couchTrackerModule, config) }

    install(CallLogging)
    install(ContentNegotiation) { json() }
    install(RequestValidation)
    install(Authentication) {
        JWT.Login.install(this, applicationData)
    }
    install(StatusPages) {
        exception<IgnoreException> { _, _ -> }
    }
    install(Resources)

    routing {
        route("/api") {
            get {
                call.respond(applicationData.apiInfo)
            }
            authRoutes(applicationData)
            authenticate(JWT.Login.ACCESS) {
                showRoutes(applicationData)
            }
        }
    }
}

class IgnoreException : RuntimeException()