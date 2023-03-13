package com.github.couchtracker.server.model.api.users.lists

import com.github.couchtracker.server.model.common.externalIds.ExternalId
import com.github.couchtracker.server.model.db.list.ListItem
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class PatchListItem {
    abstract val id: ExternalId
    abstract fun toListItem(added: Instant): ListItem
    abstract infix fun sameAs(other: ListItem): Boolean

    @Serializable
    @SerialName("show")
    data class Show(override val id: ExternalId) : PatchListItem() {
        override fun toListItem(added: Instant) = ListItem.Show(id = id, added = added)
        override fun sameAs(other: ListItem) = other is ListItem.Show && id == other.id
    }
}

operator fun List<PatchListItem>.contains(li: ListItem): Boolean {
    return any { it sameAs li }
}
