package com.github.couchtracker.server.common.serializers

import kotlin.reflect.KClass
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.serializer

@OptIn(ExperimentalSerializationApi::class, InternalSerializationApi::class)
@Suppress("UnnecessaryAbstractClass")
abstract class SingleFieldSerializer<T, F : Any>(
    name: String,
    fieldType: KClass<F>,
    private val getField: T.() -> F,
    private val deserialize: (F) -> T,
) : KSerializer<T> {

    private val delegate = fieldType.serializer()

    override val descriptor = SerialDescriptor(name, delegate.descriptor)

    override fun serialize(encoder: Encoder, value: T) {
        encoder.encodeSerializableValue(delegate, value.getField())
    }

    override fun deserialize(decoder: Decoder): T {
        return deserialize(decoder.decodeSerializableValue(delegate))
    }
}
