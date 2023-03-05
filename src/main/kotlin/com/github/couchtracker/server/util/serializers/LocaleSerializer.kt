package com.github.couchtracker.server.util.serializers

import java.util.Locale

object LocaleSerializer : RegexSerializer<Locale>(
    name = "Locale",
    regex = "([a-z]{2,8})(?:-([A-Z]{2}))?".toRegex(),
    serialize = {
        require(it.language.isNotBlank())
        require(it.script.isNullOrEmpty())
        require(it.country.all { c -> c in 'A'..'Z' })
        require(it.variant.isNullOrEmpty())
        require(it.extensionKeys.isEmpty())

        buildString {
            append(it.language)
            if (it.country.isNotEmpty()) {
                append("-")
                append(it.country)
            }
        }
    },
    deserializeRegex = { matcher ->
        Locale(
            matcher.groupValues[1],
            matcher.groupValues[2],
        )
    },
)
