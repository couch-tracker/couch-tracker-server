package com.github.couchtracker.server.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = ExternalId.Serializer::class)
data class ExternalId(val provider: ExternalIdProvider, val id: String) {

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
            val decodedString = decoder.decodeString()
            val match = REGEX.matchEntire(decodedString)
                ?: throw SerializationException("Invalid input '$decodedString', should be in the format '<provider>:<id>'")
            return ExternalId(ExternalIdProvider(match.groupValues[1]), match.groupValues[2])
        }
    }
}

@JvmInline
value class ExternalIdProvider(val value: String) {
    init {
        require(value.all { it in 'a'..'z' })
    }

    override fun toString() = value
}