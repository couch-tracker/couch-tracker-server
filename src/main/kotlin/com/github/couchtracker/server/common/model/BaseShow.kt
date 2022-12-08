package com.github.couchtracker.server.common.model

import kotlinx.serialization.Serializable

@Serializable
data class BaseShow(
    val id: ExternalId,
    val name: Translations,
    val externalIds: ShowExternalIds,
    val status: ShowStatus?,
    val ratings: ShowRatings,
)
