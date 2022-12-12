package com.github.couchtracker.server.common.serializers

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.serializer
import kotlin.reflect.KClass

@OptIn(ExperimentalSerializationApi::class, InternalSerializationApi::class)
abstract class SingleFieldSerializer<T, F : Any>(
    name: String,
    fieldType: KClass<F>,
    private val getField: T.() -> F,
    private val deserialize: (F) -> T,
) : KSerializer<T> {

    private val delegate = fieldType.serializer()

    override val descriptor: SerialDescriptor = object : SerialDescriptor by delegate.descriptor {
        override val serialName = name
    }

    override fun serialize(encoder: Encoder, value: T) {
        return delegate.serialize(encoder, value.getField())
    }

    override fun deserialize(decoder: Decoder): T {
        return deserialize(delegate.deserialize(decoder))
    }
}