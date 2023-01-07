package com.github.couchtracker.server.model

import kotlinx.serialization.Serializable

val SUPPORTED_LANGUAGES = listOf("da", "nl", "en", "fi", "fr", "de", "hu", "it", "nb", "pt", "ro", "ru", "es", "sv", "tr")

typealias Translations = List<Translation>

@Serializable
data class Translation(
    val language: String,
    val value: String,
) {
    init {
        check(language in SUPPORTED_LANGUAGES)
    }
}

fun translationsOf(vararg translations: Pair<String, String>): Translations {
    return translations.map { Translation(it.first, it.second) }
}