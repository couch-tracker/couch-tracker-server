package com.github.couchtracker.server

import com.mongodb.ConnectionString
import java.lang.IllegalStateException

object Config {

    object Mongo {
        val connectionUrl = ConnectionString(getEnv("MONGODB_CONNECTION_URL", "mongodb://localhost:27017"))
        val databaseName = getEnv("MONGODB_DATABASE", "couch-tracker")
    }

    object Web {
        val port = getEnv("WEB_PORT", "8080").toInt()
    }


    private fun getEnvOrNull(name: String) : String? {
        return System.getenv()[name]
    }

    private fun getEnv(name : String, defaultValue: String) : String {
        return getEnvOrNull(name) ?: defaultValue
    }

    private fun getEnv(name : String) : String {
        return getEnvOrNull(name) ?: throw IllegalStateException("Environment variable $name not found!")
    }
}