package com.github.couchtracker.server.util

import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class Username(val value: String) {

    init {
        runValidations {
            validate(value.length >= MIN_LENGTH) { "Username must be at least $MIN_LENGTH characters long" }
            validate(value.length <= MAX_LENGTH) { "Username can be at most $MAX_LENGTH characters long" }
            validate(value.all { it in VALID_CHARACTERS }) {
                "Username can only contain alphanumerical characters and these special chars: $SPECIAL_CHARS"
            }
            validate(value.firstOrNull() !in SPECIAL_CHARS) { "Username must start with an alphanumerical character" }
            validate(value.lastOrNull() !in SPECIAL_CHARS) { "Username must end with an alphanumerical character" }
        }.require()
    }

    companion object {
        private const val MIN_LENGTH = 4
        private const val MAX_LENGTH = 24
        private val SPECIAL_CHARS = listOf('-', '_')
        private val VALID_CHARACTERS = ('a'..'z') + ('A'..'Z') + ('0'..'9') + SPECIAL_CHARS
    }
}
