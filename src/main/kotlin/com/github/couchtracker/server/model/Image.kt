package com.github.couchtracker.server.model

import com.github.couchtracker.server.common.serializers.LocaleSerializer
import java.util.Locale
import kotlinx.serialization.Serializable

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
