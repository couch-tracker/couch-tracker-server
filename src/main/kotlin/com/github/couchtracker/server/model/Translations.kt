package com.github.couchtracker.server.model

import com.github.couchtracker.server.common.serializers.LocaleSerializer
import java.util.Locale
import kotlinx.serialization.Serializable

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
