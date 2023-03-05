package com.github.couchtracker.server

import com.github.couchtracker.server.config.Config
import com.github.couchtracker.server.infoProviders.InfoProviders
import com.github.couchtracker.server.infoProviders.tmdb.Tmdb
import com.github.couchtracker.server.model.api.ApiInfo
import com.github.couchtracker.server.model.api.toType
import com.github.couchtracker.server.model.db.ShowDbo
import com.github.couchtracker.server.model.db.ShowOrderingDbo
import com.github.couchtracker.server.model.db.UserDbo
import org.litote.kmongo.coroutine.CoroutineClient
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import org.litote.kmongo.serialization.configuration as kmongoSerializationConfiguration

private val DBOS = setOf(
    ShowDbo,
    ShowOrderingDbo,
    UserDbo,
)

class ApplicationData(
    val mongoClient: CoroutineClient,
    val db: CoroutineDatabase,
    val config: Config,
    val infoProviders: InfoProviders,
) {

    // TODO have mechanism to know patch number
    val apiInfo = ApiInfo(1, 0, config.signup.toType())

    companion object {
        suspend fun create(scope: CoroutineScope, config: Config): ApplicationData = coroutineScope {
            kmongoSerializationConfiguration = kmongoSerializationConfiguration.copy(
                classDiscriminator = "type",
            )
            val mongoClient = KMongo.createClient(config.mongo.connectionUrl).coroutine
            val db = mongoClient.getDatabase(config.mongo.databaseName)
            DBOS.map { launch { it.setup(db) } }.joinAll()

            ApplicationData(
                mongoClient = mongoClient,
                db = db,
                config = config,
                infoProviders = InfoProviders(
                    setOfNotNull(
                        config.tmdb?.let { Tmdb(it, scope) },
                    ),
                ),
            )
        }
    }
}
