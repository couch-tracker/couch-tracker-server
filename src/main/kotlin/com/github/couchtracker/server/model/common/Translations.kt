package com.github.couchtracker.server.model.common

import com.github.couchtracker.server.util.serializers.LocaleSerializer
import java.util.Locale
import kotlinx.serialization.Serializable

private typealias LocaleMatcher = (Locale?) -> Boolean

@Serializable
data class Translations(
    val translations: List<Translation>,
    @Serializable(with = LocaleSerializer::class)
    val originalLocale: Locale?, // null when unknown
) {

    init {
        val map = translations.groupBy { it.locale }
        val doubleTranslations = map.filter { it.value.size > 1 }
        require(doubleTranslations.isEmpty()) {
            "More than one translation provided for the locales: ${doubleTranslations.keys.joinToString()}"
        }
        if (originalLocale != null) {
            require(originalLocale in map) {
                "Original locale must be in translations"
            }
        }
    }

    fun forLocales(locales: List<Locale?>): String? {
        val matchers = locales.toMatchers()
        return translations
            .mapNotNull {
                when (val score = it.score(matchers)) {
                    null -> null
                    else -> it to score
                }
            }
            .minByOrNull { it.second }
            ?.first
            ?.value
    }

    fun forLocalesOrOriginal(locales: List<Locale>): String? {
        return forLocales(locales + originalLocale)
    }
}

@Serializable
data class Translation(
    @Serializable(with = LocaleSerializer::class)
    val locale: Locale?, // null when unknown
    val value: String,
)

private fun Translation.score(matchers: List<LocaleMatcher>): Int? {
    val index = matchers.indexOfFirst { it(locale) }
    return when {
        index >= 0 -> index
        else -> null
    }
}

private fun List<Locale?>.toMatchers(): List<LocaleMatcher> = flatMap { locale ->
    buildList {
        add { it == locale }
        if (locale != null && locale.country.isBlank()) {
            add { it != null && it.language == locale.language }
        }
    }
}
