package com.github.couchtracker.server.util

import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class Password(val value: String) {

    fun validate() = runValidations {
        validate(value.length >= MIN_LENGTH) { "Password must be at least $MIN_LENGTH characters long" }
        validate(!FORBIDDEN_CHARACTERS.containsMatchIn(value)) {
            "Password contains invalid characters. Only printable ASCII characters are allowed"
        }
        validate(CHARACTER_SETS.count { it.containsMatchIn(value) } >= MIN_CHARACTER_SETS) {
            "Password must contain at least three from: lowercase, uppercase, numbers, symbols"
        }
    }

    companion object {
        private const val MIN_LENGTH = 8
        private const val MIN_CHARACTER_SETS = 3
        private val CHARACTER_SETS = listOf(
            "[a-z]".toRegex(),
            "[A-Z]".toRegex(),
            "[0-9]".toRegex(),
            "[^a-zA-Z0-9]".toRegex(),
        )

        // Only non-control ASCII are allowed
        private val FORBIDDEN_CHARACTERS = "[^\\u0020-\\u007E]".toRegex()
    }
}
