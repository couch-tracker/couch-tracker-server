package com.github.couchtracker.server.util.serializers

import io.ktor.http.Url

object UrlSerializer : StringSerializer<Url>(
    name = "Url",
    serialize = { it.toString() },
    deserialize = { Url(it) },
)
