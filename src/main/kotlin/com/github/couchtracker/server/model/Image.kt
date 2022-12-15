package com.github.couchtracker.server.model

import com.github.couchtracker.server.common.serializers.LocaleSerializer
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class Image(
    val width: Int,
    val height: Int,
    @Serializable(with = LocaleSerializer::class)
    val language: Locale?,
    val url: String,
    val ratings: ImageRatings,
)

@Serializable
data class ImageRatings(
    val tmdb: Rating.Tmdb? = null,
)