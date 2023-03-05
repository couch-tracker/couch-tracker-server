package com.github.couchtracker.server.util.serializers

import kotlinx.serialization.SerializationException

@Suppress("UnnecessaryAbstractClass")
abstract class RegexSerializer<T : Any>(
    name: String,
    regex: Regex,
    serialize: (T) -> String = { it.toString() },
    val deserializeRegex: (MatchResult) -> T,
) : StringSerializer<T>(
    name = name,
    serialize = serialize,
    deserialize = {
        deserializeRegex(regex.matchEntire(it) ?: throw SerializationException("Invalid input '$it' for deserializing $name"))
    },
)
