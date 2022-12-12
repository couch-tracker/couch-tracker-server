package com.github.couchtracker.server.infoProviders.tmdb

import com.github.couchtracker.server.model.ExternalId


@JvmInline
value class TmdbShowId(val value: Int) {
    init {
        require(value > 0)
    }

    fun toExternalId() = ExternalId(Tmdb.EXTERNAL_ID_PROVIDER, value.toString())
}