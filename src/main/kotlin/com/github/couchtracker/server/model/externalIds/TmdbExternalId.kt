package com.github.couchtracker.server.model.externalIds

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException

@Serializable(with = TmdbExternalId.Serializer::class)
data class TmdbExternalId(val id: Int) : ExternalId() {

    override fun serializeData() = id.toString()

    fun <T> to(constructor: (Int) -> T) = constructor(id)

    object Serializer : ExternalIdSubclassSerializer<TmdbExternalId>(
        cls = TmdbExternalId::class,
        deserialize = { TmdbExternalId(it.toIntOrNull() ?: throw SerializationException("TMDB ID must be integer")) }
    )
}