package com.github.couchtracker.server.model

import com.github.couchtracker.server.model.db.UserDbo
import de.bwaldvogel.mongo.MongoServer
import de.bwaldvogel.mongo.backend.memory.MemoryBackend
import io.kotest.common.runBlocking
import io.kotest.core.spec.style.FunSpec
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.coroutine.insertOne
import org.litote.kmongo.newId
import org.litote.kmongo.reactivestreams.KMongo


class TestMongoTest : FunSpec(
    {

        val mongo = MongoServer(MemoryBackend())
        val connectionString = mongo.bindAndGetConnectionString()

        val client = KMongo.createClient(connectionString).coroutine
        val db = client.getDatabase("couch-tracker")

        test("tetst") {
            runBlocking {
                UserDbo.collection(db).insertOne(
                    UserDbo(
                        id = newId(),
                        username = "Test  username",
                        email = "test@email",
                        password = "abcd",
                        name = "Name",
                    ),
                )

                println(UserDbo.collection(db).find().toList())
            }
        }
    },
)
