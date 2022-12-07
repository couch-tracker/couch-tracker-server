package com.github.couchtracker.server.db.model

import com.github.couchtracker.server.common.model.ExternalId
import com.github.couchtracker.server.common.model.Translations
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ShowOrderingDbo(
    @SerialName("_id")
    val id : ExternalId,
    val show : ExternalId,
    val name: Translations,
    val description: Translations,
)
