package com.github.couchtracker.server.model.api.users.lists

import com.github.couchtracker.server.model.common.externalIds.ExternalId
import com.github.couchtracker.server.model.common.list.ListSorterType
import com.github.couchtracker.server.util.OptionalField
import com.github.couchtracker.server.util.map
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class PatchShowCollectionBody(
    @SerialName("append")
    val appendShows: OptionalField<List<ExternalId>> = OptionalField.Missing,
    @SerialName("remove")
    val removeShows: OptionalField<List<ExternalId>> = OptionalField.Missing,
    @SerialName("sort")
    val sortShows: OptionalField<List<ExternalId>> = OptionalField.Missing,
    override val sorter: OptionalField<ListSorterType> = OptionalField.Missing,
) : AbstractPatchListBody {

    @Transient
    override val append: OptionalField<List<PatchListItem.Show>> = appendShows.map { list ->
        list.map { PatchListItem.Show(it) }
    }

    @Transient
    override val remove: OptionalField<List<PatchListItem.Show>> = removeShows.map { list ->
        list.map { PatchListItem.Show(it) }
    }

    @Transient
    override val sort: OptionalField<List<PatchListItem.Show>> = sortShows.map { list ->
        list.map { PatchListItem.Show(it) }
    }
}
