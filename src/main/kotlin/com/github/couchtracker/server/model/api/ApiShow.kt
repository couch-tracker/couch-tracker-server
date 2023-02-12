package com.github.couchtracker.server.model.api

import com.github.couchtracker.server.model.common.ShowExternalIds
import com.github.couchtracker.server.model.common.ShowRatings
import com.github.couchtracker.server.model.common.ShowStatus
import com.github.couchtracker.server.model.common.externalIds.ExternalId
import kotlinx.serialization.Serializable

@Serializable
data class ApiShow(
    val id: ExternalId,
    val name: String,
    val externalIds: ShowExternalIds,
    val status: ShowStatus?,
    val ratings: ShowRatings,
)
