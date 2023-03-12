package com.github.couchtracker.server.infoProviders.tmdb

import com.uwetrottmann.tmdb2.entities.Configuration

data class TmdbConfiguration(
    val images: TmdbImageSizesConfiguration,
)

fun Configuration.toTmdbConfiguration(): TmdbConfiguration {
    return TmdbConfiguration(
        images = (images ?: error("TMDB configuration missing images configuration")).toTmdbImageSizesConfiguration(),
    )
}
