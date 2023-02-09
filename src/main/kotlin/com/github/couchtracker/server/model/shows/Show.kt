package com.github.couchtracker.server.model.shows

import com.github.couchtracker.server.model.Translations
import com.github.couchtracker.server.model.externalIds.ExternalId
import kotlinx.serialization.Serializable

@Serializable
data class Show(
    val id: ExternalId,
    val name: Translations,
    val externalIds: ShowExternalIds,
    val status: ShowStatus?,
    val ratings: ShowRatings,
)
