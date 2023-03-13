package com.github.couchtracker.server.model.api.users.lists

import kotlinx.serialization.Serializable

@Serializable
data class CreateListBody(val name: ListName)
