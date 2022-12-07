package com.github.couchtracker.server.common.model

import com.github.couchtracker.server.common.serializers.LocaleSerializer
import kotlinx.serialization.Serializable
import java.util.Locale

typealias Translations = List<Translation>

@Serializable
data class Translation(
    @Serializable(with = LocaleSerializer::class)
    val language: Locale,
    val value: String
)

fun translationsOf(vararg translations: Pair<String, String>): Translations {
    return translations.map { Translation(Locale.forLanguageTag(it.first), it.second) }
}