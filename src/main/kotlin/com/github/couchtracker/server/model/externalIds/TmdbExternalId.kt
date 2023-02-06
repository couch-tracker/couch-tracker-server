package com.github.couchtracker.server.model.externalIds

import com.github.couchtracker.server.infoProviders.InfoProviders
import com.github.couchtracker.server.infoProviders.tmdb.Tmdb
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException

@Serializable(with = TmdbExternalId.Serializer::class)
data class TmdbExternalId(val id: Long) : ExternalId() {

    override fun getInfoProvider(infoProviders: InfoProviders) = infoProviders.get<Tmdb>()

    override fun serializeData() = id.toString()

    fun <T> to(constructor: (Long) -> T) = constructor(id)

    object Serializer : ExternalIdSubclassSerializer<TmdbExternalId>(
        cls = TmdbExternalId::class,
        deserialize = { TmdbExternalId(it.toLongOrNull() ?: throw SerializationException("TMDB ID must be integer")) }
    )
}