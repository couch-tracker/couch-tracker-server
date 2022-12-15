package com.github.couchtracker.server.model

import kotlinx.serialization.Serializable


sealed class Rating {
    @Serializable
    // Private constructor leak issue: https://youtrack.jetbrains.com/issue/KT-11914
    data class Tmdb private constructor(val average: Double, val count: Long) : Rating() {

        init {
            require(average >= 0)
            require(count >= 0L)
            if (count == 0L) {
                require(average == 0.0)
            }
        }

        companion object {
            operator fun invoke(average: Double, count: Long) = Tmdb(
                average = if (count == 0L) 0.0 else average,
                count = count
            )
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