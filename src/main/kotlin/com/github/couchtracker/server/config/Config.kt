package com.github.couchtracker.server.config

import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.Secret
import com.sksamuel.hoplite.addFileSource
import com.sksamuel.hoplite.sources.EnvironmentVariablesPropertySource

data class Config(
    val logLevel: LogLevel = LogLevel.INFO,
    val tmdb: Tmdb? = null,
    val mongo: Mongo,
    val port: Int = 80,
    val host: String = "0.0.0.0",
) {
    data class Mongo(
        val connectionUrl: String,
        val databaseName: String = "couch-tracker",
    )

    data class Tmdb(
        val apiKey: Secret,
    )

    companion object {
        fun load(): Config {
            return ConfigLoaderBuilder
                .default()
                .addPropertySource(
                    EnvironmentVariablesPropertySource(
                        useUnderscoresAsSeparator = true,
                        allowUppercaseNames = true,
                    )
                )
                .addFileSource("/couch-tracker.toml", optional = true, allowEmpty = true)
                .addFileSource("couch-tracker-dev-config.toml", optional = true, allowEmpty = true)
                .build()
                .loadConfigOrThrow()
        }
    }
}
