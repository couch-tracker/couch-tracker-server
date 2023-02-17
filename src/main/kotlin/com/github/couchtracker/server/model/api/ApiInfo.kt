package com.github.couchtracker.server.model.api

import com.github.couchtracker.server.config.SignupConfig
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApiInfo(
    val version: Int,
    val patch: Int,
    val signup: SignupType,
) {
    val name: String = "couch-tracker"
}

enum class SignupType {
    @SerialName("closed")
    CLOSED,

    @SerialName("open")
    OPEN;
}

fun SignupConfig.toType() = when (this) {
    is SignupConfig.Open -> SignupType.OPEN
    is SignupConfig.Closed -> SignupType.CLOSED
}
