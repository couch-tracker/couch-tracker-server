package com.github.couchtracker.server.model.db.list

import com.github.couchtracker.server.ApplicationData
import com.github.couchtracker.server.model.api.users.lists.ApiListItem
import com.github.couchtracker.server.model.common.externalIds.ExternalId
import java.util.Locale
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class ListItem {

    abstract val id: ExternalId
    abstract val added: Instant

    abstract suspend fun toApiItem(ad: ApplicationData, locales: List<Locale>): ApiListItem?

    @Serializable
    @SerialName("show")
    data class Show(
        override val id: ExternalId,
        override val added: Instant,
    ) : ListItem() {

        override suspend fun toApiItem(ad: ApplicationData, locales: List<Locale>): ApiListItem.Show? {
            val tvApis = id.getInfoProvider(ad.infoProviders)?.tvApis ?: return null
            return tvApis
                .show(id)
                .info
                .loadOrDownload(ad.db)
                .toApiShow(locales)
                .let { ApiListItem.Show(it, added) }
        }
    }
}

operator fun List<ListItem>.contains(eid: ExternalId): Boolean {
    return any { it.id == eid }
}
