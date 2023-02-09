package com.github.couchtracker.server.config

import com.sksamuel.hoplite.Secret

data class JwtConfig(
    val secret: Secret,
)
