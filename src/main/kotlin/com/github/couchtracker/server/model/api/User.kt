package com.github.couchtracker.server.model.api

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String,
    val username: String,
    val email: String,
    val name: String,
)
