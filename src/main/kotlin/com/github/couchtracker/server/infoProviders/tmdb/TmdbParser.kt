package com.github.couchtracker.server.infoProviders.tmdb

import com.github.couchtracker.server.model.*
import com.github.couchtracker.server.model.shows.*
import com.uwetrottmann.tmdb2.entities.Image as TmdbImage
import com.uwetrottmann.tmdb2.entities.Images
import com.uwetrottmann.tmdb2.entities.TvShow
import com.uwetrottmann.tmdb2.entities.Videos
import com.uwetrottmann.tmdb2.enumerations.VideoType.*
import java.util.*
import com.uwetrottmann.tmdb2.entities.Translations as TmdbTranslations
import com.uwetrottmann.tmdb2.enumerations.VideoType as TmdbVideoType

object TmdbParser {

    fun status(status: String) = when (status) {
        "Ended" -> ShowStatus.ENDED
        "Returning Series" -> ShowStatus.CONTINUING
        else -> TODO()
    }

    fun videoProvider(site: String) = when (site) {
        "Vimeo" -> VideoProvider.VIMEO
        "YouTube" -> VideoProvider.YOUTUBE
        else -> null
    }
}

fun TmdbTranslations.toDbTranslations(map: (TmdbTranslations.Translation.Data) -> String): Translations {
    return this.translations
        .filter { it.iso_639_1 in SUPPORTED_LANGUAGES }
        .mapNotNull {
            val translation = map(it.data)
            if (translation.isBlank()) {
                null
            } else Translation(
                language = it.iso_639_1,
                value = map(it.data)
            )
        }
}

fun TmdbVideoType.toVideoType() = when (this) {
    TRAILER -> VideoType.TRAILER
    TEASER -> VideoType.TEASER
    CLIP -> VideoType.CLIP
    FEATURETTE -> VideoType.FEATURETTE
    OPENING_CREDITS -> VideoType.OPENING_CREDITS
}

fun Images.toShowImages() = ShowImages<Image>(
    posters = this.posters.toImages(),
    backdrops = this.backdrops.toImages(),
    // TODO logos not exposed by library
)

fun List<TmdbImage>.toImages(): List<Image> {
    return this.map { it.toImage() }
}

fun TmdbImage.toImage(): Image {
    return Image(
        width = width,
        height = height,
        language = if (iso_639_1 == null) null else Locale(iso_639_1),
        url = "https://image.tmdb.org/t/p/original$file_path",
        ratings = ImageRatings(
            tmdb = Rating.Tmdb(vote_average, vote_count.toLong())
        )
    )
}


fun Videos.toVideos(): List<Video> {
    return this.results.mapIndexedNotNull { index, video -> video.toVideo(index) }
}

private fun Videos.Video.toVideo(index: Int): Video? {
    val provider = TmdbParser.videoProvider(site) ?: return null
    return Video(
        provider = provider,
        key = key,
        type = type.toVideoType(),
        duration = null,
        language = iso_639_1, //TODO: fare meglio?
        date = null, // TODO: technically, the API has this info, is the library that lacks it
        sortingWeight = index.toFloat(),
    )
}

fun TvShow.toShow(): Show {
    return Show(
        id = TmdbShowId(id).toExternalId(),
        name = translations.toDbTranslations { it.name },
        externalIds = ShowExternalIds(
            tmdb = id.toLong(),
            tvdb = external_ids.tvdb_id?.toLong(),
            imdb = external_ids.imdb_id,
        ),
        status = status?.let { TmdbParser.status(it) },
        ratings = ShowRatings(
            tmdb = Rating.Tmdb(vote_average, vote_count.toLong())
        ),
    )
}
