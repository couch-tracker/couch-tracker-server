package com.github.couchtracker.server.infoProviders.tmdb

import com.github.couchtracker.server.model.ExternalId
import com.github.couchtracker.server.model.ExternalIdProvider.TMDB


@JvmInline
value class TmdbShowId(val value: Int) {
    init {
        require(value > 0)
    }

    fun toExternalId() = ExternalId(TMDB, value.toString())
}