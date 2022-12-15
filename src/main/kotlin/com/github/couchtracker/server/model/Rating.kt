package com.github.couchtracker.server.model

import kotlinx.serialization.Serializable


sealed class Rating {
    @Serializable
    data class Tmdb(val average: Double, val count: Long) : Rating() {
        init {
            require(average >= 0)
            require(count >= 0L)
            if (count == 0L) {
                require(average == 0.0)
            }
        }
    }

    @Serializable
    data class Tvdb(val value: Double, val count: Long) : Rating() {
        init {
            require(value >= 0)
            require(count >= 0L)
            if (count == 0L) {
                require(value == 0.0)
            }
        }
    }
}