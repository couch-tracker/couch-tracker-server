package com.github.couchtracker.server.infoProviders.tmdb

import com.github.couchtracker.server.Config
import com.github.couchtracker.server.infoProviders.InfoProvider
import com.github.couchtracker.server.infoProviders.ids.TmdbShowId
import com.github.couchtracker.server.model.externalIds.ExternalId
import com.github.couchtracker.server.model.externalIds.TmdbExternalId
import kotlinx.coroutines.CoroutineScope
import com.uwetrottmann.tmdb2.Tmdb as TmdbClient

class Tmdb(config: Config.Tmdb, scope: CoroutineScope) : InfoProvider {

    private val client = TmdbClient(config.apiKey.value)

    override val tvApis = TmdbTvApis(client, scope).convert<ExternalId> {
        require(it is TmdbExternalId)
        TmdbShowId(it.id)
    }
}