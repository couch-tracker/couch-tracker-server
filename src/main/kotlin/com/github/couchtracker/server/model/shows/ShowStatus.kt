package com.github.couchtracker.server.model.shows

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class ShowStatus {
    @SerialName("canceled")
    CANCELED,

    @SerialName("continuing")
    CONTINUING,

    @SerialName("ended")
    ENDED,

    @SerialName("in_production")
    IN_PRODUCTION,

    @SerialName("pilot_canceled")
    PILOT_CANCELED,

    @SerialName("planned")
    PLANNED()
}