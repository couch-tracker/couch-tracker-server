package com.github.couchtracker.server.model.api.users.lists

import com.github.couchtracker.server.model.common.list.ListDisplayOptions

interface AbstractApiList {
    val displayOptions: ListDisplayOptions
    fun items(): List<ApiListItem>
}
