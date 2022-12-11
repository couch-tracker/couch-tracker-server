package com.github.couchtracker.server.api.tmdb

import com.github.couchtracker.server.common.model.shows.Show
import com.github.couchtracker.server.common.model.*
import com.github.couchtracker.server.common.model.shows.ShowExternalIds
import com.github.couchtracker.server.common.model.shows.ShowRatings
import com.github.couchtracker.server.common.model.shows.ShowStatus
import com.uwetrottmann.tmdb2.entities.Translations as TmdbTranslations
import com.uwetrottmann.tmdb2.entities.Translations.Translation.Data
import com.uwetrottmann.tmdb2.entities.TvShow

fun TvShow.toShow(): Show {
   return Show(
        id = ExternalId("tmdb", this.id.toString()),
        name = translations.toDbTranslations { it.name },
        externalIds = ShowExternalIds(
            tmdb = id.toLong(),
            tvdb = external_ids.tvdb_id?.toLong(),
            imdb = external_ids.imdb_id,
        ),
        status = status?.let { ShowStatus.fromTmdbStatus(it) },
        ratings = ShowRatings(
            tmdb = Rating.Tmdb(vote_average, vote_count.toLong())
        ),
    )
}

fun TmdbTranslations.toDbTranslations(map: (Data) -> String): Translations {
    return this.translations
        .filter { it.iso_639_1 in SUPPORTED_LANGUAGES }
        .mapNotNull {
            val translation = map(it.data)
            if (translation.isBlank()) {
                null
            } else Translation(
                language = it.iso_639_1,
                value = map(it.data)
            )
        }
}

fun ShowStatus.Companion.fromTmdbStatus(status: String) = when (status) {
    "Ended" -> ShowStatus.ENDED
    "Returning Series" -> ShowStatus.CONTINUING
    else -> TODO()
}