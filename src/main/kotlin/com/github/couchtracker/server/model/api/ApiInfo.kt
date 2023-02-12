package com.github.couchtracker.server.model.api

import kotlinx.serialization.Serializable

@Serializable
data class ApiInfo(
    val version: Int,
    val patch: Int,
) {
    val name: String = "couch-tracker"
}
