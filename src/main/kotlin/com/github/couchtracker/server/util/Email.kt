package com.github.couchtracker.server.util

import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class Email(val value: String) {

    init {
        runValidations {
            validate(value.length >= MIN_LENGTH) { "Email must have at least $MIN_LENGTH characters" }
            validate('@' in value) { "Email must contain @ sign" }
        }.require()
    }

    companion object {
        private const val MIN_LENGTH = 3
    }
}
