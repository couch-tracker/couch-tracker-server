package com.github.couchtracker.server.common.serializers

import kotlinx.serialization.SerializationException

abstract class RegexSerializer<T>(
    name: String,
    regex: Regex,
    serialize: (T) -> String = { it.toString() },
    deserialize: (MatchResult) -> T,
) : StringSerializer<T>(
    name = name,
    serialize = serialize,
    deserialize = { deserialize(regex.matchEntire(it) ?: throw SerializationException("Invalid input '$it' for deserializing $name")) }
)