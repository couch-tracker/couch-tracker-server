package com.github.couchtracker.server.common.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = ExternalId.Serializer::class)
data class ExternalId(val provider: String, val id: String) {

    init {
        require(provider.all { it in 'a'..'z' })
    }

    override fun toString(): String {
        return "${provider}:${id}"
    }

    object Serializer : KSerializer<ExternalId> {
        private val REGEX = "([a-z]+):(.+)".toRegex()

        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("ExternalId", PrimitiveKind.STRING)

        override fun serialize(encoder: Encoder, value: ExternalId) {
            encoder.encodeString(value.toString())
        }

        override fun deserialize(decoder: Decoder): ExternalId {
            val match = REGEX.matchEntire(decoder.decodeString())
            check(match != null)
            return ExternalId(match.groupValues[1], match.groupValues[2])
        }
    }
}

fun TmdbId(id : Int) = ExternalId("tmdb", id.toString())