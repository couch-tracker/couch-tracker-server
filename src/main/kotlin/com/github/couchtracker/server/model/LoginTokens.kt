package com.github.couchtracker.server.model

import kotlinx.serialization.Serializable

@Serializable
data class LoginTokens(val token: String, val refreshToken: String)