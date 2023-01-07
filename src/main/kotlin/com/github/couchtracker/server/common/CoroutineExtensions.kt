package com.github.couchtracker.server.common

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.ExperimentalCoroutinesApi

suspend fun <T> CompletableDeferred<T>.reply(f: suspend () -> T) {
    val value = try {
        f()
    } catch (e: Throwable) {
        completeExceptionally(e)
        return
    }
    complete(value)
}

/**
 * Whether this Job has completed successfully.
 *
 * Will throw [IllegalStateException] if the Job hasn't completed yet.
 *
 * @see [kotlinx.coroutines.Job.isCompleted]`
 */
@OptIn(ExperimentalCoroutinesApi::class)
fun <T> Deferred<T>.hasCompletedSuccessfully() = getCompletionExceptionOrNull() == null