package com.github.couchtracker.server.model.api.users.lists

import com.github.couchtracker.server.util.OptionalField
import kotlinx.serialization.Serializable

@Serializable
data class PatchListBody(
    val name: OptionalField<ListName> = OptionalField.Missing,
    override val append: OptionalField<List<PatchListItem>> = OptionalField.Missing,
    override val remove: OptionalField<List<PatchListItem>> = OptionalField.Missing,
    override val sort: OptionalField<List<PatchListItem>> = OptionalField.Missing,
    override val displayOptions: ListDisplayOptionsPatch = ListDisplayOptionsPatch(),
) : AbstractPatchListBody
