package com.github.couchtracker.server.util

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = OptionalField.Serializer::class)
sealed class OptionalField<out T> {

    object Missing : OptionalField<Nothing>()

    data class Present<T>(val value: T) : OptionalField<T>()

    class Serializer<T>(private val elementSerializer: KSerializer<T>) : KSerializer<OptionalField<T>> {

        override val descriptor = elementSerializer.descriptor

        override fun deserialize(decoder: Decoder): OptionalField<T> {
            return Present(elementSerializer.deserialize(decoder))
        }

        override fun serialize(encoder: Encoder, value: OptionalField<T>) {
            require(value is Present)
            elementSerializer.serialize(encoder, value.value)
        }
    }
}

fun <T> OptionalField<T>.validate(validator: (T) -> Unit) {
    if (this is OptionalField.Present) {
        validator(value)
    }
}
