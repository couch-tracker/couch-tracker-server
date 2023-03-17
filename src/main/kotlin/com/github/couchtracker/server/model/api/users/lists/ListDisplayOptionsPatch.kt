package com.github.couchtracker.server.model.api.users.lists

import com.github.couchtracker.server.model.common.list.ListDisplayOptions
import com.github.couchtracker.server.model.common.list.ListSorterType
import com.github.couchtracker.server.util.OptionalField
import com.github.couchtracker.server.util.or
import kotlinx.serialization.Serializable

@Serializable
data class ListDisplayOptionsPatch(
    val sorter: OptionalField<ListSorterType> = OptionalField.Missing,
    val group: OptionalField<Boolean> = OptionalField.Missing,
) {

    fun patched(options: ListDisplayOptions): OptionalField<ListDisplayOptions> {
        if (sorter is OptionalField.Missing && group is OptionalField.Missing) {
            // Optimize case where display options are not modified
            return OptionalField.Missing
        }
        return OptionalField.Present(
            ListDisplayOptions(
                sorter = sorter.or(options.sorter),
                group = group.or(options.group),
            ),
        )
    }
}
