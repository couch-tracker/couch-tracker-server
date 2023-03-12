package com.github.couchtracker.server.infoProviders.tmdb

import com.github.couchtracker.server.model.common.Resolution
import com.uwetrottmann.tmdb2.entities.Configuration
import io.ktor.http.Url
import kotlinx.serialization.Serializable

data class TmdbImageSizesConfiguration(
    val backdrops: TmdbImageSizesConfigurationForType,
    val posters: TmdbImageSizesConfigurationForType,
    val logos: TmdbImageSizesConfigurationForType,
    val stills: TmdbImageSizesConfigurationForType,
    val profiles: TmdbImageSizesConfigurationForType,
)

data class TmdbImageSizesConfigurationForType(
    val baseUrl: Url,
    val sizes: Set<TmdbImageSize>,
)

@Serializable
sealed class TmdbImageSize(val path: String) {

    abstract fun getSize(original: Resolution.Size): Resolution.Size

    object Original : TmdbImageSize("original") {
        override fun getSize(original: Resolution.Size) = original
    }

    data class Width(val width: Int) : TmdbImageSize("w$width") {
        override fun getSize(original: Resolution.Size): Resolution.Size {
            return Resolution.Size(width, (original.height * width) / original.width)
        }
    }

    data class Height(val height: Int) : TmdbImageSize("h$height") {
        override fun getSize(original: Resolution.Size): Resolution.Size {
            return Resolution.Size((original.width * height) / original.height, height)
        }
    }
}

fun Configuration.ImagesConfiguration.toTmdbImageSizesConfiguration(): TmdbImageSizesConfiguration {
    val baseUrl = secure_base_url ?: error("TMDB images configuration must have secure base URL")
    return TmdbImageSizesConfiguration(
        backdrops = backdrop_sizes.toTmdbImageSizesConfigurationForType(baseUrl),
        posters = poster_sizes.toTmdbImageSizesConfigurationForType(baseUrl),
        logos = logo_sizes.toTmdbImageSizesConfigurationForType(baseUrl),
        stills = still_sizes.toTmdbImageSizesConfigurationForType(baseUrl),
        profiles = profile_sizes.toTmdbImageSizesConfigurationForType(baseUrl),
    )
}

private fun List<String>?.toTmdbImageSizesConfigurationForType(baseUrl: String): TmdbImageSizesConfigurationForType {
    if (this == null) {
        error("TMDB images configuration doesn't have required sizes")
    }
    return TmdbImageSizesConfigurationForType(
        baseUrl = Url(baseUrl),
        sizes = mapNotNull {
            when {
                it == "original" -> TmdbImageSize.Original
                it.startsWith("w") -> TmdbImageSize.Width(it.substring(1).toInt())
                it.startsWith("h") -> TmdbImageSize.Height(it.substring(1).toInt())
                else -> null
            }
        }.toSet(),
    )
}
