package com.github.couchtracker.server.common.serializers

import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class Password(val value: String) {

    fun validate() = value.length >= 8
}