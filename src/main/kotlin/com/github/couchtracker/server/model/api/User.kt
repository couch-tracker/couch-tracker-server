package com.github.couchtracker.server.model.api

import com.github.couchtracker.server.util.Email
import com.github.couchtracker.server.util.Username
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String,
    val username: Username,
    val email: Email,
    val name: String,
)
