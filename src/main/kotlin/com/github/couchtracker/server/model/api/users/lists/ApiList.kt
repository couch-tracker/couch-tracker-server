package com.github.couchtracker.server.model.api.users.lists

import com.github.couchtracker.server.model.common.list.ListDisplayOptions
import kotlinx.serialization.Serializable

@Serializable
class ApiList(
    val id: String,
    val name: String,
    override val displayOptions: ListDisplayOptions,
    val items: List<ApiListItem>,
) : AbstractApiList {
    override fun items() = items
}
