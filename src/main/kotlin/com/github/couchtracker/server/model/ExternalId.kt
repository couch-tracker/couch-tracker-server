package com.github.couchtracker.server.model

import com.github.couchtracker.server.common.serializers.RegexSerializer
import kotlinx.serialization.Serializable

@Serializable(with = ExternalId.Serializer::class)
data class ExternalId(val provider: ExternalIdProvider, val id: String) {

    init {
        require(id.isNotBlank())
    }

    override fun toString(): String {
        return "${provider}:${id}"
    }
    object Serializer : RegexSerializer<ExternalId>(
        name = "ExternalId",
        regex = "([a-z]+):(.+)".toRegex(),
        deserialize = { ExternalId(ExternalIdProvider(it.groupValues[1]), it.groupValues[2]) }
    )
}

enum class ExternalIdProvider {

    TMDB;

    val id = name.lowercase()

    override fun toString() = id

    companion object {
        operator fun invoke(str: String) = ExternalIdProvider.values()
            .singleOrNull { it.id == str }
            ?: throw IllegalArgumentException("Invalid external ID provider: $str")
    }
}