package com.github.couchtracker.server.config

import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.addFileSource
import com.sksamuel.hoplite.sources.EnvironmentVariablesPropertySource

data class Config(
    val logLevel: LogLevel = LogLevel.INFO,
    val tmdb: TmdbConfig? = null,
    val mongo: MongoConfig,
    val port: Int = 80,
    val host: String = "0.0.0.0",
    val jwt: JwtConfig,
    val argon2: Argon2Config = Argon2Config(),
    val signup: SignupConfig = SignupConfig.Closed,
) {

    companion object {
        fun load(): Config {
            return ConfigLoaderBuilder
                .default()
                .addPropertySource(
                    EnvironmentVariablesPropertySource(
                        useUnderscoresAsSeparator = true,
                        allowUppercaseNames = true,
                    ),
                )
                // .addResourceSource() TODO: put default config in resources
                .addFileSource("/config/couch-tracker.toml", optional = true, allowEmpty = true)
                .addFileSource("/etc/couch-tracker/couch-tracker.toml", optional = true, allowEmpty = true)
                .addFileSource("couch-tracker-dev-config.local.toml", optional = true, allowEmpty = true)
                .addFileSource("couch-tracker-dev-config.toml", optional = true, allowEmpty = true)
                .build()
                .loadConfigOrThrow()
        }
    }
}
