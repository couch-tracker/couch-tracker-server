package com.github.couchtracker.server

import ch.qos.logback.classic.Logger
import com.github.couchtracker.server.config.Config
import com.github.couchtracker.server.routes.authRoutes
import com.github.couchtracker.server.routes.showRoutes
import com.github.couchtracker.server.routes.users.users
import com.github.couchtracker.server.util.serializers.LocaleSerializer
import com.github.couchtracker.server.util.serializers.convertWithSerializer
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.authenticate
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.callloging.CallLogging
import io.ktor.server.plugins.compression.Compression
import io.ktor.server.plugins.compression.condition
import io.ktor.server.plugins.compression.gzip
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.dataconversion.DataConversion
import io.ktor.server.plugins.requestvalidation.RequestValidation
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.request.uri
import io.ktor.server.resources.Resources
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import org.slf4j.LoggerFactory
import kotlinx.coroutines.runBlocking

fun main() {
    val config = Config.load()
    // TODO log level is set too late, so some logs are always printed no matter the logLevel
    (LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME) as Logger).level = config.logLevel.level

    embeddedServer(
        Netty,
        port = config.port,
        host = config.host,
        module = { couchTrackerModule(config) },
    ).start(wait = true)
}

fun Application.couchTrackerModule(config: Config) {
    val applicationData = runBlocking { ApplicationData.create(this@couchTrackerModule, config) }

    install(CallLogging)
    install(DataConversion) {
        convertWithSerializer(LocaleSerializer)
    }
    install(Compression) {
        gzip {
            // Disable for sensitive APIs. See https://en.wikipedia.org/wiki/BREACH
            condition { !request.uri.startsWith("/api/auth") }
        }
    }
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
                users(applicationData)
            }
        }
    }
}

class IgnoreException : RuntimeException()
