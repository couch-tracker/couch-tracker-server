package com.github.couchtracker.server.config

data class MongoConfig(
    val connectionUrl: String,
    val databaseName: String = "couch-tracker",
)