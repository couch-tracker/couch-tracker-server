package com.github.couchtracker.server.model.api.users

import com.github.couchtracker.server.util.Password
import kotlinx.serialization.Serializable

@Serializable
data class DeleteUserBody(
    val password: Password,
)
