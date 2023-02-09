package com.github.couchtracker.server.common.serializers

import java.util.Locale

object LocaleSerializer : RegexSerializer<Locale>(
    name = "Locale",
    regex = "([a-z]+)(?:[_\\-]([a-z]+))?".toRegex(RegexOption.IGNORE_CASE),
    deserialize = { matcher ->
        Locale(
            matcher.groupValues[1],
            matcher.groupValues[2].takeIf { it.isNotBlank() },
        )
    },
)
