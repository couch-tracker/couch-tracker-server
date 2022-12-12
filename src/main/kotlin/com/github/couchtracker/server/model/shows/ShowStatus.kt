package com.github.couchtracker.server.model.shows

import com.github.couchtracker.server.common.serializers.EnumIdSerializer
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

    object Serializer : EnumIdSerializer<ShowStatus>(
        enumClass = ShowStatus::class,
        getId = { id }
    )
}