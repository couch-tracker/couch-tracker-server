package com.github.couchtracker.server.infoProviders.tmdb

import com.github.couchtracker.server.Config
import com.github.couchtracker.server.model.ExternalIdProvider
import com.github.couchtracker.server.infoProviders.InfoProvider
import kotlinx.coroutines.CoroutineScope
import com.uwetrottmann.tmdb2.Tmdb as TmdbClient

class Tmdb(config: Config.Tmdb, scope: CoroutineScope) : InfoProvider {

    private val client = TmdbClient(config.apiKey.value)

    override val externalIdProvider = ExternalIdProvider.TMDB

    override val tvApis = TmdbTvApis(client, scope).convert<String> {
        val tmdbId = it.toIntOrNull()
        requireNotNull(tmdbId) { "TMDB ID must be an integer!" }
        TmdbShowId(tmdbId)
    }
}