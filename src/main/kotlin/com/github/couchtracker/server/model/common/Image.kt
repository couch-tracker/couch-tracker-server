@file:UseSerializers(
    LocaleSerializer::class,
    UrlSerializer::class,
)

package com.github.couchtracker.server.model.common

import com.github.couchtracker.server.util.serializers.LocaleSerializer
import com.github.couchtracker.server.util.serializers.UrlSerializer
import io.ktor.http.Url
import java.util.Locale
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

/**
 * Represents an image.
 * @param width in pixels
 * @param height in pixels
 * @param locale the locale of the image. If the image is not associated with any particular locale, use [NO_LOCALE]
 * @param url URL pointing at the resource
 * @param ratings ratings for this image
 */
@Serializable
data class Image(
    override val locale: Locale,
    val ratings: ImageRatings,
    val sources: List<ImageSource>,
) : LocalizedItem {

    init {
        require(sources.isNotEmpty())
    }

    companion object {
        val BEST_COMPARATOR = compareByDescending<Image> { it.ratings.score() }
            .thenByDescending { it.ratings.count() }
    }
}

@Serializable
data class ImageSource(
    val width: Int,
    val height: Int,
    val url: Url,
) {
    init {
        require(width > 0)
        require(height > 0)
    }
}

@Serializable
data class ImageRatings(
    val tmdb: Rating.Tmdb? = null,
) {
    fun score(): Double {
        return tmdb?.average ?: 0.0
    }

    fun count(): Long {
        return tmdb?.count ?: 0L
    }
}

typealias Images = MultiLocalized<Image>
typealias BestImages = SingleLocalized<Image>

fun Images.getBestImages(): BestImages = toSingle(Image.BEST_COMPARATOR)
