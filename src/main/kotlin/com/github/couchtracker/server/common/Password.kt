package com.github.couchtracker.server.common

import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class Password(val value: String) {

    fun validate() = value.length >= MAX_LENGTH

    companion object {
        private const val MAX_LENGTH = 8
    }
}
