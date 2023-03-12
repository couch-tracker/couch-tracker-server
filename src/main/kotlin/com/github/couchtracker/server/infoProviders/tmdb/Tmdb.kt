package com.github.couchtracker.server.infoProviders.tmdb

import com.github.couchtracker.server.config.TmdbConfig
import com.github.couchtracker.server.infoProviders.InfoProvider
import com.github.couchtracker.server.infoProviders.ids.TmdbShowId
import com.github.couchtracker.server.model.common.externalIds.ExternalId
import com.github.couchtracker.server.model.common.externalIds.TmdbExternalId
import com.github.couchtracker.server.util.makeNotNull
import retrofit2.await
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.retry
import kotlinx.coroutines.flow.shareIn
import com.uwetrottmann.tmdb2.Tmdb as TmdbClient

class Tmdb(config: TmdbConfig, scope: CoroutineScope) : InfoProvider {

    private val client = TmdbClient(config.apiKey.value)

    override val tvApis = TmdbTvApis(client, this, scope).convert<ExternalId> {
        require(it is TmdbExternalId)
        TmdbShowId(it.id)
    }

    private val configuration = flow {
        while (true) {
            emit(client.configurationService().configuration().makeNotNull().await())
            delay(1.days)
        }
    }
        .retry {
            delay(1.seconds)
            true
        }
        .map { it.toTmdbConfiguration() }
        .shareIn(scope, SharingStarted.Eagerly, 1)

    suspend fun configuration(): TmdbConfiguration {
        return configuration.first()
    }
}
