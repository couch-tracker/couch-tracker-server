package com.github.couchtracker.server.model.common

import kotlinx.serialization.Serializable

@Serializable
data class ShowImages(
    val posters: List<Image>,
    val backdrops: List<Image>,
    val logos: List<Image>,
)
