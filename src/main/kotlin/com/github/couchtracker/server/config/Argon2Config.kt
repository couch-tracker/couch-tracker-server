package com.github.couchtracker.server.config

import com.github.couchtracker.server.common.*
import com.github.couchtracker.server.common.ByteUnit.KI
import com.github.couchtracker.server.common.serializers.Password
import de.mkammerer.argon2.Argon2Factory
import de.mkammerer.argon2.Argon2Helper
import mu.KotlinLogging
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

private const val MIN_ITERATIONS = 10
private val MIN_MAX_DURATION = 200.milliseconds
private val MAX_MAX_DURATION = Int.MAX_VALUE.toLong().milliseconds
private val MIN_MEMORY_COST = 64L.kiB
private const val MIN_PARALLELISM = 1

private val logger = KotlinLogging.logger { }

data class Argon2Config(
    val iterations: Int? = null,
    val maxDuration: Duration = 1.seconds,
    val memoryCost: ByteSize = 64L.MiB,
    val parallelism: Int = 1,
) {
    private val argon2 = Argon2Factory.create()

    init {
        require(iterations == null || iterations >= MIN_ITERATIONS) {
            "Argon2 iterations must be >= $MIN_ITERATIONS"
        }
        require(maxDuration >= MIN_MAX_DURATION) {
            "Argon2 max duration must be >= ${MIN_MAX_DURATION.inWholeMilliseconds}ms"
        }
        require(maxDuration <= MAX_MAX_DURATION) {
            "Argon2 max duration must be <= ${MAX_MAX_DURATION.inWholeMilliseconds}ms"
        }
        require(memoryCost >= MIN_MEMORY_COST) {
            "Argon2 memory cost must be >= $MIN_MEMORY_COST"
        }
        require(parallelism >= MIN_PARALLELISM) {
            "Argon2 parallelism must be >= $MIN_PARALLELISM"
        }
    }

    private val memoryCostKiB = memoryCost.convert(KI).value.toInt() // Calling toInt() is safe because the maximum memory cost is Int.MAX_VALUE kiB

    @OptIn(ExperimentalTime::class)
    private val actualIterations = run {
        iterations ?: run {
            logger.info { "Detecting argon number of iterations (maxDuration: ${maxDuration.inWholeMilliseconds} ms, memoryCost: $memoryCost, parallelism: $parallelism)..." }
            val (iterations, time) = measureTimedValue {
                Argon2Helper.findIterations(argon2, maxDuration.inWholeMilliseconds, memoryCostKiB, parallelism)
            }
            logger.info { "Detected argon number of iterations ($iterations) in ${time.inWholeSeconds} seconds" }
            iterations
        }
    }

    fun hash(password: Password): String {
        return argon2.hash(actualIterations, memoryCostKiB, parallelism, password.value.toCharArray())
    }

    fun needsRehash(hash: String): Boolean {
        return argon2.needsRehash(hash, actualIterations, memoryCostKiB, parallelism)
    }

    fun verify(hash: String, password: Password): Boolean {
        return argon2.verify(hash, password.value.toCharArray())
    }
}