package com.github.couchtracker.server.util

import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class Username(val value: String) {
    init {
        require('@' !in value) {
            "Username cannot contain @ sign"
        }
    }
}
