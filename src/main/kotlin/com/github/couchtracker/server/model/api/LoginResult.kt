package com.github.couchtracker.server.model.api

import com.github.couchtracker.server.model.api.users.ApiUser
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class LoginResult(
    val accessToken: TokenInfo,
    val refreshToken: TokenInfo,
    val user: ApiUser,
)

@Serializable
data class TokenInfo(
    val token: String,
    val expiration: Instant,
)
