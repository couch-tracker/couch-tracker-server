package com.github.couchtracker.server.model.common.list

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class ListSortDirection {
    @SerialName("asc")
    ASC,

    @SerialName("desc")
    DESC,
}
