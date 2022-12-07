package com.github.couchtracker.server.common.model

import kotlinx.serialization.Serializable



sealed class Rating {
    @Serializable
    data class Tmdb(val value: Float, val count: Long) : Rating()

    @Serializable
    data class Tvdb(val value: Float, val count: Long) : Rating()
}