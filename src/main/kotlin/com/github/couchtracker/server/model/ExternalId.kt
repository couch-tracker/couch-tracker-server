package com.github.couchtracker.server.model

import com.github.couchtracker.server.common.serializers.RegexSerializer
import kotlinx.serialization.Serializable

@Serializable(with = ExternalId.Serializer::class)
data class ExternalId(val provider: ExternalIdProvider, val id: String) {

    override fun toString(): String {
        return "${provider}:${id}"
    }
    object Serializer : RegexSerializer<ExternalId>(
        name = "ExternalId",
        regex = "([a-z]+):(.+)".toRegex(),
        deserialize = { ExternalId(ExternalIdProvider(it.groupValues[1]), it.groupValues[2]) }
    )
}

@JvmInline
value class ExternalIdProvider(val value: String) {
    init {
        require(value.all { it in 'a'..'z' })
    }

    override fun toString() = value
}