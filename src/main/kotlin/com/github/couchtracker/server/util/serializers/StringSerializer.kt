package com.github.couchtracker.server.util.serializers

import io.ktor.util.converters.DataConversion
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Suppress("UnnecessaryAbstractClass")
abstract class StringSerializer<T : Any>(
    name: String,
    val serialize: (T) -> String = { it.toString() },
    val deserialize: (String) -> T,
) : KSerializer<T> {

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(name, PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: T) {
        encoder.encodeString(serialize(value))
    }

    override fun deserialize(decoder: Decoder): T {
        return deserialize(decoder.decodeString())
    }
}

inline fun <reified T : Any> DataConversion.Configuration.convertWithSerializer(serializer: StringSerializer<T>) {
    convert {
        decode { serializer.deserialize(it.single()) }
        encode { listOf(serializer.serialize(it)) }
    }

    convert<List<T>> {
        decode { it.map(serializer.deserialize) }
        encode { it.map(serializer.serialize) }
    }
}
