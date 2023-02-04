package com.github.couchtracker.server.common

import kotlinx.serialization.Serializable

@Serializable
data class ApiInfo(
    val version: Int,
    val patch: Int,
) {
    val name: String = "couch-tracker"
}