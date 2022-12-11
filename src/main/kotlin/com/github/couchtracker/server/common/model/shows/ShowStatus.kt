package com.github.couchtracker.server.common.model.shows

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = ShowStatus.Serializer::class)
enum class ShowStatus(val id: String) {
    CANCELED("canceled"),
    CONTINUING("continuing"),
    ENDED("ended"),
    IN_PRODUCTION("in_production"),
    PILOT_CANCELED("pilot_canceled"),
    PLANNED("planned");

    object Serializer : KSerializer<ShowStatus> {

        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("ShowStatus", PrimitiveKind.STRING)

        override fun serialize(encoder: Encoder, value: ShowStatus) {
            encoder.encodeString(value.id)
        }

        override fun deserialize(decoder: Decoder): ShowStatus {
            val str = decoder.decodeString()
            return ShowStatus.values().single { it.id == str }
        }
    }
}