package com.github.couchtracker.server.config

import com.sksamuel.hoplite.Secret

data class TmdbConfig(
    val apiKey: Secret,
)
