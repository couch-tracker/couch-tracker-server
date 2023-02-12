package com.github.couchtracker.server.util.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Suppress("UnnecessaryAbstractClass")
abstract class StringSerializer<T>(
    name: String,
    private val serialize: (T) -> String = { it.toString() },
    private val deserialize: (String) -> T,
) : KSerializer<T> {

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(name, PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: T) {
        encoder.encodeString(serialize(value))
    }

    override fun deserialize(decoder: Decoder): T {
        return deserialize(decoder.decodeString())
    }
}
