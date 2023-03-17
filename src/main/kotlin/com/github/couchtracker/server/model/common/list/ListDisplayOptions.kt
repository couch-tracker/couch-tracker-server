package com.github.couchtracker.server.model.common.list

import kotlinx.serialization.Serializable

@Serializable
data class ListDisplayOptions(
    val sorter: ListSorterType,
    val group: Boolean,
) {
    companion object {
        val DEFAULT = ListDisplayOptions(
            sorter = ListSorterType.Manual(ListSortDirection.ASC),
            group = false,
        )
    }
}
