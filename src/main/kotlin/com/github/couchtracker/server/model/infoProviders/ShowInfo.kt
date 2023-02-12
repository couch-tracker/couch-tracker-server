package com.github.couchtracker.server.model.infoProviders

import com.github.couchtracker.server.model.api.ApiShow
import com.github.couchtracker.server.model.common.ShowExternalIds
import com.github.couchtracker.server.model.common.ShowRatings
import com.github.couchtracker.server.model.common.ShowStatus
import com.github.couchtracker.server.model.common.Translations
import com.github.couchtracker.server.model.common.externalIds.ExternalId
import java.util.Locale

data class ShowInfo(
    val id: ExternalId,
    val name: Translations,
    val externalIds: ShowExternalIds,
    val status: ShowStatus?,
    val ratings: ShowRatings,
) {

    fun toApiShow(locales: List<Locale>) = ApiShow(
        id = id,
        name = name.forLocalesOrOriginal(locales).orEmpty(),
        externalIds = externalIds,
        status = status,
        ratings = ratings,
    )
}
