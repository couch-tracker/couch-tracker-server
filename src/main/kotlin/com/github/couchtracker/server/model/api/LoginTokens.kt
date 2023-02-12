package com.github.couchtracker.server.model.api

import kotlinx.serialization.Serializable

@Serializable
data class LoginTokens(val token: String, val refreshToken: String)
