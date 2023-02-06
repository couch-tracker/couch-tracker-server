package com.github.couchtracker.server.model

import com.github.couchtracker.server.common.serializers.LocaleSerializer
import kotlinx.serialization.Serializable
import java.util.Locale

typealias Translations = List<Translation>

@Serializable
data class Translation(
    @Serializable(with = LocaleSerializer::class)
    val locale: Locale,
    val value: String,
)

fun translationsOf(vararg translations: Pair<Locale, String>): Translations {
    return translations.map { Translation(it.first, it.second) }
}