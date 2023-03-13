package com.github.couchtracker.server.model.api.users.lists

import com.github.couchtracker.server.model.common.list.ListDisplayOptions
import kotlinx.serialization.Serializable

@Serializable
class ApiShowCollection(
    override val displayOptions: ListDisplayOptions,
    val shows: List<ApiListItem.Show>,
) : AbstractApiList {
    override fun items() = shows
}
