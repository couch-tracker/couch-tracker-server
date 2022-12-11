package com.github.couchtracker.server.common.model.shows

import com.github.couchtracker.server.common.model.Rating
import kotlinx.serialization.Serializable

@Serializable
data class ShowRatings(
    val tmdb : Rating.Tmdb? = null,
    val tvdb : Rating.Tvdb? = null,
)