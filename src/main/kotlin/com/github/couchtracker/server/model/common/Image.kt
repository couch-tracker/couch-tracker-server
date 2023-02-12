package com.github.couchtracker.server.model.common

import com.github.couchtracker.server.util.serializers.LocaleSerializer
import io.ktor.http.URLParserException
import io.ktor.http.Url
import java.util.Locale
import kotlinx.serialization.Serializable

@Serializable
data class Image(
    val width: Int,
    val height: Int,
    @Serializable(with = LocaleSerializer::class)
    val language: Locale?,
    val url: String, // TODO use Url
    val ratings: ImageRatings,
) {
    init {
        require(width > 0)
        require(height > 0)
        try {
            Url(url)
        } catch (e: URLParserException) {
            throw IllegalArgumentException("Invalid URL: $url", e)
        }
    }
}

@Serializable
data class ImageRatings(
    val tmdb: Rating.Tmdb? = null,
)
