package com.github.couchtracker.server

import com.github.couchtracker.server.db.model.ShowDbo
import com.github.couchtracker.server.db.model.ShowOrderingDbo
import com.github.couchtracker.server.db.model.UserDbo
import com.github.couchtracker.server.api.tmdb.TmdbApisCache
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

private val DBOS = setOf(
    ShowDbo,
    ShowOrderingDbo,
    UserDbo,
)

class ApplicationData(
    val connection: CoroutineDatabase,
    val tmdbApis: TmdbApisCache?,
) {
    companion object {
        suspend fun create(scope: CoroutineScope): ApplicationData = coroutineScope {
            val client = KMongo.createClient(Config.Mongo.connectionUrl).coroutine
            val db = client.getDatabase(Config.Mongo.databaseName)
            DBOS.map { launch { it.setup(db) } }.joinAll()

            ApplicationData(
                db,
                Config.Tmdb.client()?.let { TmdbApisCache(it, scope) },
            )
        }
    }
}