package com.github.couchtracker.server.util.serializers

import org.litote.kmongo.id.StringId

class StringIdSerializer<T> : StringSerializer<StringId<T>>(
    name = "StringId",
    serialize = { it.toString() },
    deserialize = { StringId(it) },
)
