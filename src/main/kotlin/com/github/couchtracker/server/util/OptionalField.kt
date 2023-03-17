package com.github.couchtracker.server.util

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = OptionalField.Serializer::class)
sealed class OptionalField<out T> {

    object Missing : OptionalField<Nothing>()

    data class Present<T>(val value: T) : OptionalField<T>()

    @OptIn(ExperimentalSerializationApi::class)
    class Serializer<T>(private val elementSerializer: KSerializer<T>) : KSerializer<OptionalField<T>> {

        override val descriptor = SerialDescriptor("OptionalField", elementSerializer.descriptor)

        override fun deserialize(decoder: Decoder): OptionalField<T> {
            return Present(decoder.decodeSerializableValue(elementSerializer))
        }

        override fun serialize(encoder: Encoder, value: OptionalField<T>) {
            require(value is Present)
            encoder.encodeSerializableValue(elementSerializer, value.value)
        }
    }
}

fun <T> OptionalField<T>.validate(validator: (T) -> Unit) {
    if (this is OptionalField.Present) {
        validator(value)
    }
}

fun <T, R : T> OptionalField<T>.or(ifMissing: R) = when (this) {
    is OptionalField.Missing -> ifMissing
    is OptionalField.Present -> value
}

fun <T, R> OptionalField<T>.map(map: (T) -> R) = when (this) {
    is OptionalField.Missing -> OptionalField.Missing
    is OptionalField.Present -> OptionalField.Present(map(value))
}
