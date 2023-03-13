package com.github.couchtracker.server.util.list

import com.github.couchtracker.server.model.api.users.lists.ApiListItem
import kotlin.reflect.KClass

interface ListSorter {
    suspend fun sorted(items: List<ApiListItem>): List<ApiListItem>

    fun handledTypes(): List<KClass<out ApiListItem>> = ApiListItem::class.sealedSubclasses
}
