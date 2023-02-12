package com.github.couchtracker.server.config

import com.github.couchtracker.server.util.Password
import com.sksamuel.hoplite.decoder.InformationUnit
import com.sksamuel.hoplite.decoder.SizeInBytes
import de.mkammerer.argon2.Argon2Factory

@Suppress("MagicNumber")
private val MIN_MEMORY_COST = SizeInBytes(64 * 1024)
private const val MIN_PARALLELISM = 1
private const val MIN_ITERATIONS = 1

@Suppress("MagicNumber")
private val DEFAULT_MEMORY_COST = SizeInBytes(64 * 1024 * 1024)
private const val DEFAULT_PARALLELISM = 4
private const val DEFAULT_ITERATIONS = 3

/**
 * Defaults chosen from [RFC 9106](https://www.rfc-editor.org/rfc/rfc9106.html#name-parameter-choice)
 */
data class Argon2Config(
    val iterations: Int = DEFAULT_ITERATIONS,
    val memoryCost: SizeInBytes = DEFAULT_MEMORY_COST,
    val parallelism: Int = DEFAULT_PARALLELISM,
) {
    // Calling toInt() is safe because the maximum memory cost is Int.MAX_VALUE kiB
    private val memoryCostKiB = memoryCost.convert(InformationUnit.Kibibytes).toInt()

    private val argon2 = Argon2Factory.create()
    private val randomHash = hash(Password(""))

    init {
        require(iterations >= MIN_ITERATIONS) {
            "Argon2 iterations must be >= $MIN_ITERATIONS"
        }
        require(memoryCost.size >= MIN_MEMORY_COST.size) {
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
