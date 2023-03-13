package com.github.couchtracker.server.model.api.users.lists

import kotlinx.serialization.Serializable

@JvmInline
@Serializable
value class ListName(val value: String) {
    init {
        require(value.isNotBlank()) { "List name must not be blank" }
    }
}
