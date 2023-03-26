package com.github.couchtracker.server.util

import kotlinx.serialization.Serializable

private const val MIN_LENGTH = 3

@Serializable
@JvmInline
value class Email(val value: String) {

    init {
        require(value.length >= MIN_LENGTH) { "Email must have at least 3 characters" }
        require('@' in value) { "Email must contain @ sign" }
    }
}
