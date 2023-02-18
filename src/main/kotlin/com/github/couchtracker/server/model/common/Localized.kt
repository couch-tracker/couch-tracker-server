@file:UseSerializers(
    LocaleSerializer::class,
)

package com.github.couchtracker.server.model.common

import com.github.couchtracker.server.util.serializers.LocaleSerializer
import java.util.Locale
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

private typealias LocaleMatcher = (Locale?) -> Boolean

val NO_LOCALE = Locale("zxx")

@Serializable
sealed class Localized<T : LocalizedItem> {

    abstract val items: List<T>
    abstract val originalLocale: Locale? // null when unknown

    protected fun getTranslationsWithBestScore(locales: List<Locale?>): List<T> {
        val matchers = locales.toMatchers()
        return items
            .groupBy { it.score(matchers) }
            .minByOrNull { it.key ?: Int.MAX_VALUE }
            ?.value
            .orEmpty()
    }
}

@Serializable
data class SingleLocalized<T : LocalizedItem>(
    override val items: List<T>,
    override val originalLocale: Locale?, // null when unknown
) : Localized<T>() {

    init {
        val map = items.groupBy { it.locale }
        val doubleTranslations = map.filter { it.value.size > 1 }
        require(doubleTranslations.isEmpty()) {
            "More than one translation provided for the locales: ${doubleTranslations.keys.joinToString()}"
        }
    }

    fun forLocales(locales: List<Locale?>): T? {
        return getTranslationsWithBestScore(locales).singleOrNull()
    }

    fun forLocalesOrOriginal(locales: List<Locale>): T? {
        return forLocales(locales + originalLocale)
    }
}

@Serializable
data class MultiLocalized<T : LocalizedItem>(
    override val items: List<T>,
    override val originalLocale: Locale?, // null when unknown
) : Localized<T>() {

    fun forLocales(locales: List<Locale?>): List<T> {
        return getTranslationsWithBestScore(locales)
    }

    fun forLocalesOrOriginal(locales: List<Locale>): List<T> {
        return forLocales(locales + originalLocale)
    }

    fun toSingle(comparator: Comparator<T>): SingleLocalized<T> {
        return SingleLocalized(
            items = items
                .groupBy { it.locale }
                .map { it.value.minWith(comparator) },
            originalLocale = originalLocale,
        )
    }
}

interface LocalizedItem {
    val locale: Locale? // null when unknown
}

private fun LocalizedItem.score(matchers: List<LocaleMatcher>): Int? {
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
