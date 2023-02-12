package com.github.couchtracker.server.model.common

import kotlinx.serialization.Serializable

@Serializable
data class ShowRatings(
    val tmdb: Rating.Tmdb? = null,
    val tvdb: Rating.Tvdb? = null,
)
