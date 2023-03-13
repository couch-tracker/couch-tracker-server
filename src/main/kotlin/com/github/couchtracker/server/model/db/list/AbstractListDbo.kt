package com.github.couchtracker.server.model.db.list

import com.github.couchtracker.server.ApplicationData
import com.github.couchtracker.server.model.api.users.lists.AbstractApiList
import com.github.couchtracker.server.model.api.users.lists.ApiListItem
import com.github.couchtracker.server.model.common.list.ListDisplayOptions
import com.github.couchtracker.server.model.db.UserDbo
import org.litote.kmongo.Id
import java.util.Locale

interface AbstractListDbo<ApiList : AbstractApiList> {

    val user: Id<UserDbo>
    val displayOptions: ListDisplayOptions

    fun listItems(): List<ListItem>

    suspend fun getSortedApiItems(ad: ApplicationData, locales: List<Locale>): List<ApiListItem> {
        val items = listItems().mapNotNull { it.toApiItem(ad, locales) }
        return displayOptions.sorter.getSorter().sorted(items)
    }

    suspend fun toApi(ad: ApplicationData, locales: List<Locale>): ApiList
}
