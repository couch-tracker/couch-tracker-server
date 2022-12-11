package com.github.couchtracker.server

import com.github.couchtracker.server.db.model.ShowDbo
import com.github.couchtracker.server.db.model.ShowOrderingDbo
import com.github.couchtracker.server.db.model.UserDbo
import com.github.couchtracker.server.infoProviders.InfoProvider
import com.github.couchtracker.server.infoProviders.InfoProviders
import com.github.couchtracker.server.infoProviders.tmdb.Tmdb
import com.github.couchtracker.server.infoProviders.tmdb.TmdbTvApis
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo
import com.uwetrottmann.tmdb2.Tmdb as TmdbClient

private val DBOS = setOf(
    ShowDbo,
    ShowOrderingDbo,
    UserDbo,
)

class ApplicationData(
    val connection: CoroutineDatabase,
    val infoProviders: InfoProviders,
) {
    companion object {
        suspend fun create(scope: CoroutineScope, config: Config): ApplicationData = coroutineScope {
            val client = KMongo.createClient(config.mongo.connectionUrl).coroutine
            val db = client.getDatabase(config.mongo.databaseName)
            DBOS.map { launch { it.setup(db) } }.joinAll()

            ApplicationData(
                db,
                InfoProviders(setOfNotNull(
                    config.tmdb?.let { Tmdb(it, scope) }
                )),
            )
        }
    }
}