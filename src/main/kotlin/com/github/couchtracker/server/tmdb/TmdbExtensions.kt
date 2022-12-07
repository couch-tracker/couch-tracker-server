package com.github.couchtracker.server.tmdb

import com.github.couchtracker.server.common.model.SUPPORTED_LANGUAGES
import com.github.couchtracker.server.common.model.ShowStatus
import com.github.couchtracker.server.common.model.Translation
import com.github.couchtracker.server.common.model.Translations
import com.uwetrottmann.tmdb2.entities.Translations as TmdbTranslations
import com.uwetrottmann.tmdb2.entities.Translations.Translation.Data

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