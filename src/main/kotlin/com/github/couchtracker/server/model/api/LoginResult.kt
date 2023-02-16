package com.github.couchtracker.server.model.api

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class LoginResult(
    val accessToken: TokenInfo,
    val refreshToken: TokenInfo,
    val user: User,
)

@Serializable
data class TokenInfo(
    val token: String,
    val expiration: Instant,
)
