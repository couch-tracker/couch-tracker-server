package com.github.couchtracker.server.config

import com.github.couchtracker.server.common.*
import com.github.couchtracker.server.common.ByteUnit.KI
import com.github.couchtracker.server.common.serializers.Password
import de.mkammerer.argon2.Argon2Factory

private const val MIN_ITERATIONS = 1
private val MIN_MEMORY_COST = 64L.kiB
private const val MIN_PARALLELISM = 1

/**
 * Defaults chosen from [RFC 9106](https://www.rfc-editor.org/rfc/rfc9106.html#name-parameter-choice)
 */
data class Argon2Config(
    val iterations: Int = 3,
    val memoryCost: ByteSize = 64L.MiB,
    val parallelism: Int = 4,
) {
    private val memoryCostKiB = memoryCost.convert(KI).value.toInt() // Calling toInt() is safe because the maximum memory cost is Int.MAX_VALUE kiB

    private val argon2 = Argon2Factory.create()
    private val randomHash = hash(Password(""))

    init {
        require(iterations >= MIN_ITERATIONS) {
            "Argon2 iterations must be >= $MIN_ITERATIONS"
        }
        require(memoryCost >= MIN_MEMORY_COST) {
            "Argon2 memory cost must be >= $MIN_MEMORY_COST"
        }
        require(parallelism >= MIN_PARALLELISM) {
            "Argon2 parallelism must be >= $MIN_PARALLELISM"
        }
    }


    fun hash(password: Password): String {
        return argon2.hash(iterations, memoryCostKiB, parallelism, password.value.toCharArray())
    }

    fun needsRehash(hash: String): Boolean {
        return argon2.needsRehash(hash, iterations, memoryCostKiB, parallelism)
    }

    fun verify(hash: String?, password: Password): Boolean {
        return argon2.verify(hash ?: randomHash, password.value.toCharArray()) && hash != null
    }
}