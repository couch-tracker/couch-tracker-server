package com.github.couchtracker.server.common

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.time.Duration

sealed class CacheMessages<out K, out T> {

    /**
     * Use this message to request an item
     */
    class RequestItem<K, T>(
        val key: K,
        val response: CompletableDeferred<T>? = null,
    ) : CacheMessages<K, T>()

    /**
     * When the cache actor receives this message, all invalid entries will be deleted
     */
    object PurgeInvalidEntries : CacheMessages<Nothing, Nothing>()
}

data class CacheEntry<T>(
    val result: Deferred<T>,
    val computationStarted: Instant,
)

/**
 * Request the cache actor to delete all invalid entries
 */
suspend fun SendChannel<CacheMessages<Nothing, Nothing>>.purgeInvalid() {
    send(CacheMessages.PurgeInvalidEntries)
}

/**
 * Request an item from the cache actor
 */
suspend fun <K, T> SendChannel<CacheMessages<K, T>>.get(key: K): T {
    val response = CompletableDeferred<T>()
    send(CacheMessages.RequestItem(key, response))
    return response.await()
}

/**
 * Returns a cache actor that will evict entries after the specified amount of time.
 */
fun <K, T> CoroutineScope.cacheActor(
    expireTimeForSuccess: Duration? = null,
    expireTimeForFailures: Duration? = null,
    compute: suspend (K) -> T,
): SendChannel<CacheMessages<K, T>> {
    val channel = cacheActor({ entry ->
        if (entry.result.isCompleted) {
            val elapsed = Clock.System.now() - entry.computationStarted
            if (entry.result.hasCompletedSuccessfully()) {
                expireTimeForSuccess == null || elapsed < expireTimeForSuccess
            } else {
                expireTimeForFailures == null || elapsed < expireTimeForFailures
            }
        } else true
    }, compute)

    // Clean-upper
    val periodicCleanup = listOfNotNull(
        expireTimeForSuccess, expireTimeForFailures
    ).minOrNull()
    if (periodicCleanup != null) {
        launch {
            while (isActive) {
                delay(periodicCleanup)
                channel.purgeInvalid()
            }
        }
    }

    return channel
}

/**
 * Returns a new cache actor.
 * Cache entries will be computed using [compute], and will be used until [isValid] returns `true`.
 */
fun <K, T> CoroutineScope.cacheActor(
    isValid: (CacheEntry<T>) -> Boolean = { !it.result.isCompleted || it.result.hasCompletedSuccessfully() },
    compute: suspend (K) -> T,
): SendChannel<CacheMessages<K, T>> {
    val channel = Channel<CacheMessages<K, T>>()
    val cache = mutableMapOf<K, CacheEntry<T>>()

    launch {
        supervisorScope {
            for (message in channel) when (message) {
                is CacheMessages.RequestItem -> handleRequest(isValid, compute, cache, message)
                is CacheMessages.PurgeInvalidEntries -> purgeInvalid(isValid, cache)
            }
        }
    }

    return channel
}

private fun <K, T> CoroutineScope.handleRequest(
    isValid: (CacheEntry<T>) -> Boolean,
    compute: suspend (K) -> T,
    cache: MutableMap<K, CacheEntry<T>>,
    message: CacheMessages.RequestItem<K, T>,
) {
    val entry = cache.update(message.key) { _, currentEntry ->
        if (currentEntry == null || !isValid(currentEntry)) {
            CacheEntry(
                async { compute(message.key) },
                Clock.System.now(),
            )
        } else currentEntry
    }
    if (message.response != null) {
        launch {
            message.response.reply {
                entry.result.await()
            }
        }
    }
}

private fun <T, K> purgeInvalid(
    isValid: (CacheEntry<T>) -> Boolean,
    cache: MutableMap<K, CacheEntry<T>>,
) {
    val iter = cache.iterator()
    while (iter.hasNext()) {
        val next = iter.next()
        if (!isValid(next.value)) {
            iter.remove()
        }
    }
}
