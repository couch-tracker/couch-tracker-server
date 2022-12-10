package com.github.couchtracker.server

import com.github.couchtracker.server.db.model.shows
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
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
    install(RequestValidation)
    install(ContentNegotiation) { json() }

    val ad = runBlocking { ApplicationData.create(this@couchTrackerModule, config) }

    routing {
        get("/shows") {
            call.respond(ad.connection.shows().find().toFlow().map { it.toApi() }.toList())
        }
        get("/show/{id}") {
            val showId = call.parameters.getOrFail("id").toInt()
            ad.tmdbApis ?: error("No TMDB ID provided")
            val show = ad.tmdbApis.baseShow.loadOrDownload(showId, ad.connection)
            call.respond(show)
        }
    }
}