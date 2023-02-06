package com.github.couchtracker.server.common

import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class Password(val value: String) {

    fun validate() = value.length >= 8
}