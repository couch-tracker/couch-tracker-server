package com.github.couchtracker.server.common

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred

suspend fun <T> Deferred<T>.replyTo(completableDeferred: CompletableDeferred<T>) {
    val value = try {
        await()
    } catch (e: Throwable) {
        completableDeferred.completeExceptionally(e)
        return
    }
    completableDeferred.complete(value)
}