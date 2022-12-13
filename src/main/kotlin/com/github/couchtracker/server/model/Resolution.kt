package com.github.couchtracker.server.model

import com.github.couchtracker.server.common.serializers.EnumIdSerializer
import com.github.couchtracker.server.common.serializers.RegexSerializer
import com.github.couchtracker.server.common.serializers.SingleFieldSerializer
import kotlinx.serialization.Serializable

@Serializable
sealed class Resolution {

    @Serializable(with = Class.Serializer::class)
    class Class(val enum: ResolutionClass) {

        object Serializer : SingleFieldSerializer<Class, ResolutionClass>(
            name = "Resolution.Class",
            fieldType = ResolutionClass::class,
            getField = { enum },
            deserialize = { Class(it) },
        )
    }

    @Serializable(with = Size.Serializer::class)
    class Size(val width: Int, val height: Int) {
        init {
            require(width > 0)
            require(height > 0)
        }

        override fun toString() = "${width}x${height}"

        object Serializer : RegexSerializer<Size>(
            name = "Resolution.Size",
            regex = "(\\d+)x(\\d+)".toRegex(),
            deserialize = { Size(it.groupValues[1].toInt(), it.groupValues[2].toInt()) }
        )
    }
}

@Serializable(with = ResolutionClass.Serializer::class)
enum class ResolutionClass(val id: String) {
    SD("sd"),
    HQ("hq"),
    HD("hd"),
    FHD("fhd"),
    FOUR_K("4k"),
    EIGHT_K("8k");

    object Serializer : EnumIdSerializer<ResolutionClass>(ResolutionClass::class, { id })
}