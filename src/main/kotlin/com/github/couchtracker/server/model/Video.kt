package com.github.couchtracker.server.model

import kotlin.time.Duration
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class Video(
    val provider: VideoProvider,
    val key: String,
    val type: VideoType,
    val duration: Duration?,
    val language: String?,
    // val resolution: Resolution?,
    val date: Instant?,
    val sortingWeight: Float,
)

@Serializable
enum class VideoProvider {
    YOUTUBE, VIMEO
}

@Serializable
enum class VideoType {
    TRAILER,
    TEASER,
    CLIP,
    FEATURETTE,
    OPENING_CREDITS,
}
