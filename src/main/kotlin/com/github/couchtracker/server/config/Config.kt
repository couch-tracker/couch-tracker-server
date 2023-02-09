package com.github.couchtracker.server.config

import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.addFileSource
import com.sksamuel.hoplite.sources.EnvironmentVariablesPropertySource
import mu.KotlinLogging

private val logger = KotlinLogging.logger { }

data class Config(
    val logLevel: LogLevel = LogLevel.INFO,
    val tmdb: TmdbConfig? = null,
    val mongo: MongoConfig,
    val port: Int = 80,
    val host: String = "0.0.0.0",
    val jwt: JwtConfig,
    val argon2: Argon2Config = Argon2Config(),
) {

    companion object {
        fun load(): Config {
            logger.info { "Loading config..." }
            return ConfigLoaderBuilder
                .default()
                .addPropertySource(
                    EnvironmentVariablesPropertySource(
                        useUnderscoresAsSeparator = true,
                        allowUppercaseNames = true,
                    ),
                )
                .addFileSource("/couch-tracker.toml", optional = true, allowEmpty = true)
                .addFileSource("couch-tracker-dev-config.toml", optional = true, allowEmpty = true)
                .build()
                .loadConfigOrThrow<Config>()
                .also { logger.info { "Detected configuration: $it" } }
        }
    }
}
