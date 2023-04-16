package com.github.couchtracker.server.util

import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class Password(val value: String) {

    fun validate() = runValidations {
        validate(value.length >= MIN_LENGTH) { "Password must be at least $MIN_LENGTH characters long" }
        validate(value.all { it.category !in FORBIDDEN_CHARACTER_CATEGORIES }) {
            "Password contains invalid characters: control, format, private use and unassigned unicode code points are forbidden"
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

        private val FORBIDDEN_CHARACTER_CATEGORIES = listOf(
            CharCategory.PRIVATE_USE,
            CharCategory.CONTROL,
            CharCategory.UNASSIGNED,
            CharCategory.FORMAT,
        )
    }
}
