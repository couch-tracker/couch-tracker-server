package com.github.couchtracker.server.util.list

import com.github.couchtracker.server.model.api.users.lists.ApiListItem
import kotlin.reflect.KClass

interface ListSorter {

    /**
     * This function accepts some [items] and returns a new sorted list.
     * The sorted list MAY contain a copy of the items with the [ApiListItem.sortGroup] value changed.
     *
     * Items not supported by this sorted MUST be placed as at the end of the list with a `null` sort group.
     */
    suspend fun sorted(items: List<ApiListItem>): List<ApiListItem>

    /**
     * Returns a list of item types this sorter can handle.
     */
    fun handledTypes(): List<KClass<out ApiListItem>> = ApiListItem::class.sealedSubclasses
}
