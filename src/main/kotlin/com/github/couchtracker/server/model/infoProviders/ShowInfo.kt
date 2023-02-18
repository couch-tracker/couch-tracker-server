package com.github.couchtracker.server.model.infoProviders

import com.github.couchtracker.server.model.api.ApiShow
import com.github.couchtracker.server.model.common.BestImages
import com.github.couchtracker.server.model.common.NO_LOCALE
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
    val poster: BestImages,
    val backdrop: BestImages,
) {

    fun toApiShow(locales: List<Locale>) = ApiShow(
        id = id,
        name = name.forLocalesOrOriginal(locales)?.translation.orEmpty(),
        externalIds = externalIds,
        status = status,
        ratings = ratings,
        poster = poster.forLocalesOrOriginal(locales),
        posterClean = poster.forLocales(listOf(NO_LOCALE)),
        backdrop = backdrop.forLocalesOrOriginal(locales),
        backdropClean = poster.forLocales(listOf(NO_LOCALE))
    )
}
