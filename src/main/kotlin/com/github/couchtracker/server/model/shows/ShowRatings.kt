package com.github.couchtracker.server.model.shows

import com.github.couchtracker.server.model.Rating
import kotlinx.serialization.Serializable

@Serializable
data class ShowRatings(
    val tmdb: Rating.Tmdb? = null,
    val tvdb: Rating.Tvdb? = null,
)
