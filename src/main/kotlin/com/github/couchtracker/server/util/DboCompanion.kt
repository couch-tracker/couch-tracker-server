package com.github.couchtracker.server.util

import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.coroutine.CoroutineDatabase

interface DboCompanion<T : Any> {

    fun collection(db: CoroutineDatabase): CoroutineCollection<T>

    suspend fun CoroutineCollection<T>.setup() {
        // default: noop
    }

    suspend fun setup(db: CoroutineDatabase) {
        collection(db).setup()
    }
}
