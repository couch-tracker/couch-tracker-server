package com.github.couchtracker.server

import com.github.couchtracker.server.common.update
import com.github.couchtracker.server.common.model.ExternalId
import com.github.couchtracker.server.common.replyTo
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.actor
import org.litote.kmongo.coroutine.CoroutineDatabase
import kotlin.random.Random

private suspend fun downloadShow(externalId: ExternalId): Long {
    println("Downloading")
    delay(2000)
    if (Random.nextBoolean()) throw RuntimeException("Error downloading $externalId")
    return 1
}

private suspend fun insertShow(show: Long, db: CoroutineDatabase): Long {
    println("Inserting")
    delay(50)
    return 1
}

private suspend fun loadShow(externalId: ExternalId, db: CoroutineDatabase): Long? {
    println("Loading")
    delay(50)
    return null
}

private suspend fun loadOrDownloadShow(externalId: ExternalId, db: CoroutineDatabase): Long {
    return loadShow(externalId, db) ?: insertShow(downloadShow(externalId), db)
}

class GetShowMessage(val show: ExternalId, val response: CompletableDeferred<Long>? = null)

fun CoroutineScope.showStorageActor(db: CoroutineDatabase) = actor<GetShowMessage> {
    supervisorScope {
        val jobs = mutableMapOf<ExternalId, Deferred<Long>>()

        for (msg in channel) {
            val job = jobs.update(msg.show) { _, currentJob ->
                if (currentJob == null || currentJob.isCompleted) {
                    async { loadOrDownloadShow(msg.show, db) }
                } else currentJob
            }

            if (msg.response != null) {
                launch {
                    job.replyTo(msg.response)
                }
            }
        }
    }
}
