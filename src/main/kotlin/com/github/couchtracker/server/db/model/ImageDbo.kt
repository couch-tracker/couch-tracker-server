package com.github.couchtracker.server.db.model

import com.github.couchtracker.server.common.serializers.LocaleSerializer
import com.github.couchtracker.server.db.DboCompanion
import com.github.couchtracker.server.model.ExternalId
import com.github.couchtracker.server.model.ExternalIdProvider.TMDB
import com.github.couchtracker.server.model.Image
import com.github.couchtracker.server.model.ImageRatings
import io.ktor.http.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.litote.kmongo.coroutine.CoroutineDatabase
import java.util.Locale

@Serializable
data class ImageDbo(
    val width: Int,
    val height: Int,
    @Serializable(with = LocaleSerializer::class)
    val language: Locale?,
    val url: String,
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

    fun toApi() = Image(
        width = width,
        height = height,
        language = language,
        url = url,
        ratings = ratings,
    )
}