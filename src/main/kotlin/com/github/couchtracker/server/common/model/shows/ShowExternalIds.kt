package com.github.couchtracker.server.common.model.shows

import kotlinx.serialization.Serializable

@Serializable
data class ShowExternalIds(
    val tvdb: Long? = null,
    val tmdb: Long? = null,
    val imdb: String? = null,
)