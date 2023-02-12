package com.github.couchtracker.server.config

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class SignupConfig {

    @Serializable
    @SerialName("closed")
    object Closed : SignupConfig()

//    @Serializable
//    @SerialName("invite")
//    data class Invite(val trustedUsers: String) : SignupConfig()

    @Serializable
    @SerialName("open")
    object Open : SignupConfig()
}
