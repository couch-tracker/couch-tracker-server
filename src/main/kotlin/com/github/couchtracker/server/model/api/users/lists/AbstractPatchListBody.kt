package com.github.couchtracker.server.model.api.users.lists

import com.github.couchtracker.server.model.db.list.ListItem
import com.github.couchtracker.server.model.db.list.contains
import com.github.couchtracker.server.util.OptionalField
import com.github.couchtracker.server.util.OptionalField.Missing
import com.github.couchtracker.server.util.OptionalField.Present
import com.github.couchtracker.server.util.or
import kotlinx.datetime.Clock

interface AbstractPatchListBody {
    // Change items
    val append: OptionalField<List<PatchListItem>>
    val remove: OptionalField<List<PatchListItem>>

    // Sort items
    val sort: OptionalField<List<PatchListItem>>

    // Change list options
    val displayOptions: ListDisplayOptionsPatch

    fun patchedItems(items: List<ListItem>): OptionalField<List<ListItem>> {
        if (append is Missing && remove is Missing && sort is Missing) {
            // Optimize case where items are not modified
            return Missing
        }

        val now = Clock.System.now()
        val appendItems = append.or(emptyList())
        val removeItems = remove.or(emptyList())
        val sortedItems = sort.or(null)

        return Present(
            buildList {
                // Start with adding all previous items
                this += items

                // Append all items that are not already present
                this += appendItems
                    .filterNot { it.id in items }
                    .map { it.toListItem(now) }

                // Remove items
                removeIf { it in removeItems }

                // Change items order
                if (sortedItems != null) {
                    val indexMap = mutableMapOf<ListItem, Int>()
                    fun ListItem.index(): Int {
                        // Compute index of given list item in the new sorting
                        return indexMap
                            // Get index from provided list, if any
                            .computeIfAbsent(this) { li ->
                                sortedItems.indexOfFirst { it sameAs li }
                            }
                            // If the item is not found in the list, put at the ends
                            // The sorting is stable, so we can safely return Int.MAX_VALUE for all elements
                            .takeIf { it >= 0 }
                            ?: Int.MAX_VALUE
                    }
                    // Sort the list based on the index function
                    sortBy { it.index() }
                }
            },
        )
    }
}
