package com.github.couchtracker.server

import com.github.couchtracker.server.common.model.ExternalId
import com.github.couchtracker.server.db.model.shows
import io.ktor.resources.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.resources.*
import io.ktor.server.resources.Resources
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
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

object Routes {
    @Serializable
    @Resource("/shows")
    object Shows

    @Serializable
    @Resource("/show/{id}")
    data class Show(val id: ExternalId)
}

fun Application.couchTrackerModule(config: Config) {
    install(CallLogging)
    install(RequestValidation)
    install(ContentNegotiation) { json() }
    install(Resources)

    val ad = runBlocking { ApplicationData.create(this@couchTrackerModule, config) }

    routing {
        get<Routes.Shows> {
            call.respond(ad.connection.shows().find().toFlow().map { it.toApi() }.toList())
        }
        get<Routes.Show> { resource ->
            require(resource.id.provider == "tmdb") { TODO() }
            ad.tmdbApis ?: error("No TMDB ID provided")
            val show = ad.tmdbApis.baseShow.loadOrDownload(resource.id.id.toInt(), ad.connection)
            call.respond(show)
        }
    }
}