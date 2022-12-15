package com.github.couchtracker.server.model.shows

import kotlinx.serialization.Serializable

@Serializable
data class ShowImages<I>(
    val posters: List<I> = emptyList(),
    val backdrops: List<I> = emptyList(),
    val logos: List<I> = emptyList(),
) {
    fun <T> map(transform: (I) -> T) = ShowImages(
        posters = posters.map(transform),
        backdrops = backdrops.map(transform),
        logos = backdrops.map(transform),
    )
}