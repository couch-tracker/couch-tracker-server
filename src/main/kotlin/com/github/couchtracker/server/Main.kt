package com.github.couchtracker.server

import com.github.couchtracker.server.db.setup
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.response.*
import kotlinx.coroutines.*
import org.litote.kmongo.reactivestreams.KMongo
import org.litote.kmongo.coroutine.*

fun main() {
    val client = KMongo.createClient("mongodb://localhost:27017").coroutine
    val db = client.getDatabase("couch-tracker")
    runBlocking {
        db.setup()
    }

    embeddedServer(Netty, port = 8080) {
        install(CallLogging)
        install(RequestValidation)
        install(ContentNegotiation) {
            json()
        }

        routing {
            get("/") {
                call.respondText("Hello, world!")
            }
        }
    }.start(wait = true)
}