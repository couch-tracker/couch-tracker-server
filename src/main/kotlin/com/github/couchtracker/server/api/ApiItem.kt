package com.github.couchtracker.server.api

import org.litote.kmongo.coroutine.CoroutineDatabase

abstract class ApiItem<K, T> {

    abstract suspend fun load(key: K, db: CoroutineDatabase): T?

    abstract suspend fun download(key: K): T

    suspend fun loadOrDownload(key: K, db: CoroutineDatabase): T {
        return load(key, db) ?: download(key)
    }
}