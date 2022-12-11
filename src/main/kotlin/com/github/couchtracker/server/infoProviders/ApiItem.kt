package com.github.couchtracker.server.infoProviders

import org.litote.kmongo.coroutine.CoroutineDatabase

abstract class ApiItem<T> {

    abstract suspend fun load(db: CoroutineDatabase): T?

    abstract suspend fun download(): T

    suspend fun loadOrDownload(db: CoroutineDatabase): T {
        return load(db) ?: download()
    }
}