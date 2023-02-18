package com.github.couchtracker.server.model.common

import kotlinx.serialization.Serializable

@Serializable
data class ShowImages(
    val posters: Images,
    val backdrops: Images,
    val logos: Images,
) {
    companion object {
        val EMPTY = ShowImages(
            posters = Images(emptyList(), null),
            backdrops = Images(emptyList(), null),
            logos = Images(emptyList(), null),
        )
    }
}
