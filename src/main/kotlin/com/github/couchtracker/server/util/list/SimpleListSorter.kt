package com.github.couchtracker.server.util.list

import com.github.couchtracker.server.model.api.users.lists.ApiListItem
import com.github.couchtracker.server.model.api.users.lists.ApiListItemGroupValue
import com.github.couchtracker.server.model.common.list.ListSortDirection
import com.github.couchtracker.server.model.common.list.ListSortDirection.ASC
import com.github.couchtracker.server.model.common.list.ListSortDirection.DESC

abstract class SimpleListSorter<C : Comparable<C>>(private val direction: ListSortDirection) : ListSorter {

    open fun getGroup(comparable: C): ApiListItemGroupValue? = null

    abstract fun getComparable(index: Int, item: ApiListItem): C?

    override suspend fun sorted(items: List<ApiListItem>): List<ApiListItem> {
        data class MapValue(val comparable: C?, val group: ApiListItemGroupValue?)

        // In order to avoid calling the abstract methods more times than necessary let's create a cache
        // This map maps each item to the comparable/group pair
        val map = items
            .withIndex()
            .associate { (index, item) ->
                val comparable = getComparable(index, item)
                val group = comparable?.let { getGroup(it) }
                item to MapValue(comparable, group)
            }

        // Create new sorted list based on comparable and sort direction
        val sorted = when (direction) {
            ASC -> items.sortedBy { map[it]?.comparable }
            DESC -> items.sortedByDescending { map[it]?.comparable }
        }

        // Copy each item and add the sort group
        return sorted.map { it.withSortGroup(map[it]?.group) }
    }
}
