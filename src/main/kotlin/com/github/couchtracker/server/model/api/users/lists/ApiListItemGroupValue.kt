package com.github.couchtracker.server.model.api.users.lists

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class ApiListItemGroupValue {

    @Serializable
    @SerialName("initial")
    data class Initial(val value: String) : ApiListItemGroupValue()

    @Serializable
    @SerialName("date")
    data class Date(val instant: Instant) : ApiListItemGroupValue()

    @Serializable
    @SerialName("rating")
    data class Rating(val percent: Double) : ApiListItemGroupValue()
}
