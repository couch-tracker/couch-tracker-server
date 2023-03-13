package com.github.couchtracker.server.model.api.users.lists

import com.github.couchtracker.server.model.api.ApiShow
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class ApiListItem {

    abstract val item: Any
    abstract val added: Instant

    @Serializable
    @SerialName("show")
    data class Show(
        override val item: ApiShow,
        override val added: Instant,
    ) : ApiListItem()
}
