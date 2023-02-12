package com.github.couchtracker.server.infoProviders.ids

import com.github.couchtracker.server.model.common.externalIds.TmdbExternalId

sealed interface TmdbId {
    val value: Long

    fun toExternalId() = TmdbExternalId(value)
}

@JvmInline
value class TmdbShowId(override val value: Long) : TmdbId {
    init {
        requireTmdbId(value)
    }
}

private fun requireTmdbId(id: Long) {
    require(id > 0)
}
