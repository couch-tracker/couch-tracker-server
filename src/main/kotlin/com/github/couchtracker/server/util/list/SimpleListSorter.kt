package com.github.couchtracker.server.util.list

import com.github.couchtracker.server.model.api.users.lists.ApiListItem
import com.github.couchtracker.server.model.common.list.ListSortDirection
import com.github.couchtracker.server.model.common.list.ListSortDirection.ASC
import com.github.couchtracker.server.model.common.list.ListSortDirection.DESC
import com.github.couchtracker.server.util.filterNotNullValues

abstract class SimpleListSorter<C : Comparable<C>>(private val direction: ListSortDirection) : ListSorter {

    abstract fun getComparable(index: Int, item: ApiListItem): C?

    override suspend fun sorted(items: List<ApiListItem>): List<ApiListItem> {
        val map = items
            .withIndex()
            .associateWith { (index, item) -> getComparable(index, item) }
            .filterNotNullValues()
            .mapKeys { it.key.value }

        return when (direction) {
            ASC -> items.sortedBy { map[it] }
            DESC -> items.sortedByDescending { map[it] }
        }
    }
}
