package com.github.couchtracker.server.model.common

import com.github.couchtracker.server.model.common.externalIds.ExternalId
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class ShowCollection(
    val shows: List<ShowCollectionItem>,
)

@Serializable
data class ShowCollectionItem(
    val show: ExternalId,
    val added: Instant,
)
