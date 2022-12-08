package com.github.couchtracker.server.api

import com.github.couchtracker.server.common.replyTo
import com.github.couchtracker.server.common.update
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.actor

class ApiCallCache<K, T>(scope: CoroutineScope, val call: suspend (key: K) -> T) {
    //TODO clear cache after a while
    // channel.send(ApiCacheMessage.RemoveJobMessage(message.key))

    suspend fun get(key: K): T {
        val response = CompletableDeferred<T>()
        channel.send(ApiCacheMessage.GetMessage(key, response))
        return response.await()
    }

    //TODO generalize
    private val channel = scope.actor<ApiCacheMessage<K, T>> {
        supervisorScope {
            val jobs = mutableMapOf<K, Deferred<T>>()

            for (message in channel) {
                when (message) {
                    is ApiCacheMessage.GetMessage -> {
                        val job = jobs.update(message.key) { _, currentJob ->
                            if (currentJob == null || currentJob.isCompleted) {
                                async { call(message.key) }
                            } else currentJob
                        }

                        if (message.response != null) {
                            launch {
                                job.replyTo(message.response)
                            }
                        }
                    }

                    is ApiCacheMessage.RemoveJobMessage -> {
                        @Suppress("DeferredResultUnused")
                        jobs.remove(message.key)
                    }
                }
            }
        }
    }
}

private sealed class ApiCacheMessage<K, in T> {

    class GetMessage<K, T>(val key: K, val response: CompletableDeferred<T>? = null) : ApiCacheMessage<K, T>()

    class RemoveJobMessage<K, T>(val key: K) : ApiCacheMessage<K, T>()
}