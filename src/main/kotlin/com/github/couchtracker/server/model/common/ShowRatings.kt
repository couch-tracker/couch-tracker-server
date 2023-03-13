package com.github.couchtracker.server.model.common

import kotlinx.serialization.Serializable

@Serializable
data class ShowRatings(
    val tmdb: Rating.Tmdb? = null,
    val tvdb: Rating.Tvdb? = null,
) {

    fun avg(): Double? {
        TODO("Calculate a [0,1] number that is the approximate average of all available ratings")
    }
}
