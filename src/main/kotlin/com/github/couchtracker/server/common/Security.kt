package com.github.couchtracker.server.common

import kotlinx.coroutines.delay
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

class ConstantTimeReceiver {
    var cancelled = false
        private set

    fun cancel() {
        cancelled = true
    }
}

/**
 * Executes the given [operation] and waits to return until total [duration] time has passed.
 * If [operation] takes more than [duration] to compute, the value is returned immediately.
 * Operation receives a [ConstantTimeReceiver] from which the constantTime delay can ba cancelled.
 */
@OptIn(ExperimentalTime::class)
suspend inline fun <R> constantTime(duration: Duration, operation: ConstantTimeReceiver.() -> R): R {
    val ctr = ConstantTimeReceiver()
    val (value, tookTime) = measureTimedValue {
        ctr.operation()
    }
    if (!ctr.cancelled) {
        val missingTime = duration - tookTime
        if (missingTime.isPositive()) {
            delay(missingTime)
        }
    }
    return value
}