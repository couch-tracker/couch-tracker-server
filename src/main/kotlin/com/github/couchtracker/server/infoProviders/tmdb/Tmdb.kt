package com.github.couchtracker.server.infoProviders.tmdb

import com.github.couchtracker.server.config.TmdbConfig
import com.github.couchtracker.server.infoProviders.InfoProvider
import com.github.couchtracker.server.infoProviders.ids.TmdbShowId
import com.github.couchtracker.server.model.common.externalIds.ExternalId
import com.github.couchtracker.server.model.common.externalIds.TmdbExternalId
import kotlinx.coroutines.CoroutineScope
import com.uwetrottmann.tmdb2.Tmdb as TmdbClient

class Tmdb(config: TmdbConfig, scope: CoroutineScope) : InfoProvider {

    private val client = TmdbClient(config.apiKey.value)

    override val tvApis = TmdbTvApis(client, scope).convert<ExternalId> {
        require(it is TmdbExternalId)
        TmdbShowId(it.id)
    }
}
