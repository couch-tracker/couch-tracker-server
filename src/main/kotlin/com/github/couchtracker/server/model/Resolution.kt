package com.github.couchtracker.server.model

import com.github.couchtracker.server.common.serializers.RegexSerializer
import com.github.couchtracker.server.common.serializers.SingleFieldSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class Resolution {

    @Serializable(with = Class.Serializer::class)
    data class Class(val enum: ResolutionClass) {

        object Serializer : SingleFieldSerializer<Class, ResolutionClass>(
            name = "Resolution.Class",
            fieldType = ResolutionClass::class,
            getField = { enum },
            deserialize = { Class(it) },
        )
    }

    @Serializable(with = Size.Serializer::class)
    data class Size(val width: Int, val height: Int) {
        init {
            require(width > 0)
            require(height > 0)
        }

        fun toClass(): Class? {
            // Mostly copied from: https://github.com/jellyfin/jellyfin/blob/2e4db18ebea51a3e0b2d9b822ccee3bad918173f/MediaBrowser.Model/Entities/MediaStream.cs#L595-L630
            val cls = when {
                width <= 256 && height <= 144 -> ResolutionClass.SD
                width <= 426 && height <= 240 -> ResolutionClass.SD
                width <= 640 && height <= 360 -> ResolutionClass.SD
                width <= 682 && height <= 384 -> ResolutionClass.SD
                width <= 720 && height <= 404 -> ResolutionClass.SD
                width <= 854 && height <= 480 -> ResolutionClass.HQ
                width <= 960 && height <= 544 -> ResolutionClass.HQ
                width <= 1024 && height <= 576 -> ResolutionClass.HQ
                width <= 1280 && height <= 962 -> ResolutionClass.HD
                width <= 2560 && height <= 1440 -> ResolutionClass.FHD
                width <= 4096 && height <= 3072 -> ResolutionClass.FOUR_K
                width <= 8192 && height <= 8192 -> ResolutionClass.EIGHT_K
                else -> null
            }
            return Class(cls ?: return null)
        }

        override fun toString() = "${width}x${height}"

        object Serializer : RegexSerializer<Size>(
            name = "Resolution.Size",
            regex = "(\\d+)x(\\d+)".toRegex(),
            deserialize = { Size(it.groupValues[1].toInt(), it.groupValues[2].toInt()) }
        )
    }
}

@Serializable
enum class ResolutionClass {

    @SerialName("sd")
    SD,

    @SerialName("hq")
    HQ,

    @SerialName("hd")
    HD,

    @SerialName("fhd")
    FHD,

    @SerialName("4k")
    FOUR_K,

    @SerialName("8k")
    EIGHT_K;

    @OptIn(ExperimentalSerializationApi::class)
    val id get() = serializer().descriptor.getElementName(ordinal)
}