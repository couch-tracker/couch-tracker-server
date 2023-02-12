package com.github.couchtracker.server.model.common.externalIds

import com.github.couchtracker.server.infoProviders.InfoProvider
import com.github.couchtracker.server.infoProviders.InfoProviders
import com.github.couchtracker.server.util.serializers.RegexSerializer
import kotlin.reflect.KClass
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.serializer

private val REGEX = "([a-z]+):(.+)".toRegex()

@Serializable(with = ExternalId.Serializer::class)
sealed class ExternalId {

    val type
        get() = this::class.typeName

    abstract fun serializeData(): String

    abstract fun getInfoProvider(infoProviders: InfoProviders): InfoProvider?

    object Serializer : KSerializer<ExternalId> {

        override val descriptor = PrimitiveSerialDescriptor("ExternalId", PrimitiveKind.STRING)

        @OptIn(InternalSerializationApi::class)
        override fun deserialize(decoder: Decoder): ExternalId {
            val str = decoder.decodeString()
            val matcher = REGEX.matchEntire(str)
                ?: throw SerializationException("Invalid external ID: $str")
            val (_, type, value) = matcher.groupValues

            val cls = ExternalId::class.sealedSubclasses
                .singleOrNull { it.typeName == type }
                ?: throw SerializationException("Invalid external ID type: $type")

            return (cls.serializer() as ExternalIdSubclassSerializer<*>).deserialize(value)
        }

        override fun serialize(encoder: Encoder, value: ExternalId) {
            encoder.encodeString(value.serialize())
        }
    }
}

val KClass<out ExternalId>.typeName
    get() = (this.simpleName ?: error("Class cannot be anonymous"))
        .removeSuffix("ExternalId")
        .lowercase()

private fun ExternalId.serialize(): String = "$type:${serializeData()}"

@Suppress("UnnecessaryAbstractClass")
abstract class ExternalIdSubclassSerializer<T : ExternalId>(
    cls: KClass<out T>,
    val deserialize: (data: String) -> T,
) : RegexSerializer<T>(
    name = "ExternalId.${cls.typeName}",
    regex = REGEX,
    serialize = { it.serialize() },
    deserialize = {
        val type = it.groupValues[1]
        val expectedType = cls.typeName
        if (type != expectedType) {
            throw SerializationException("External ID must be of type $expectedType, $type given")
        }
        deserialize(it.groupValues[2])
    },
) {
    init {
        requireNotNull(cls.simpleName) { "Class cannot be anonymous" }
    }
}
