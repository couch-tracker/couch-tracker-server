package com.github.couchtracker.server.infoProviders.ids

import com.github.couchtracker.server.model.externalIds.TmdbExternalId


sealed interface TmdbId {
    val value: Int

    fun toExternalId() = TmdbExternalId(value)
}

@JvmInline
value class TmdbShowId(override val value: Int) : TmdbId {
    init {
        requireTmdbId(value)
    }
}

private fun requireTmdbId(id: Int) {
    require(id > 0)
}