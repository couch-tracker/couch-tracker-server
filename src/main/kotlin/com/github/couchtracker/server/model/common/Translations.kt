package com.github.couchtracker.server.model.common

import com.github.couchtracker.server.util.serializers.LocaleSerializer
import java.util.Locale
import kotlinx.serialization.Serializable

typealias Translations = SingleLocalized<Translation>

@Serializable
data class Translation(
    @Serializable(with = LocaleSerializer::class)
    override val locale: Locale?,
    val translation: String,
) : LocalizedItem
