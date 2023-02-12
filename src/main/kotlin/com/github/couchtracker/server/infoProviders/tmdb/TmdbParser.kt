package com.github.couchtracker.server.infoProviders.tmdb

import com.github.couchtracker.server.infoProviders.ids.TmdbShowId
import com.github.couchtracker.server.model.common.Image
import com.github.couchtracker.server.model.common.ImageRatings
import com.github.couchtracker.server.model.common.Rating
import com.github.couchtracker.server.model.common.ShowExternalIds
import com.github.couchtracker.server.model.common.ShowImages
import com.github.couchtracker.server.model.common.ShowRatings
import com.github.couchtracker.server.model.common.ShowStatus
import com.github.couchtracker.server.model.common.Translation
import com.github.couchtracker.server.model.common.Translations
import com.github.couchtracker.server.model.common.Video
import com.github.couchtracker.server.model.common.VideoProvider
import com.github.couchtracker.server.model.common.VideoType
import com.github.couchtracker.server.model.infoProviders.ShowInfo
import com.uwetrottmann.tmdb2.entities.Images
import com.uwetrottmann.tmdb2.entities.TvShow
import com.uwetrottmann.tmdb2.entities.Videos
import com.uwetrottmann.tmdb2.enumerations.VideoType.CLIP
import com.uwetrottmann.tmdb2.enumerations.VideoType.FEATURETTE
import com.uwetrottmann.tmdb2.enumerations.VideoType.OPENING_CREDITS
import com.uwetrottmann.tmdb2.enumerations.VideoType.TEASER
import com.uwetrottmann.tmdb2.enumerations.VideoType.TRAILER
import java.util.Locale
import com.uwetrottmann.tmdb2.entities.Image as TmdbImage
import com.uwetrottmann.tmdb2.entities.Translations as TmdbTranslations
import com.uwetrottmann.tmdb2.enumerations.VideoType as TmdbVideoType

object TmdbParser {

    fun status(status: String?) = when (status) {
        "Ended" -> ShowStatus.ENDED
        "Returning Series" -> ShowStatus.CONTINUING
        else -> TODO()
    }

    fun videoProvider(site: String?) = when (site) {
        "Vimeo" -> VideoProvider.VIMEO
        "YouTube" -> VideoProvider.YOUTUBE
        else -> null
    }
}

fun localeOrNull(language: String?, country: String?): Locale? {
    if (language.isNullOrEmpty()) return null
    return Locale(
        language,
        country.orEmpty(),
    )
}

fun TmdbTranslations?.toDbTranslations(
    original: String?,
    originalLanguage: String?,
    originalCountry: String?,
    map: (TmdbTranslations.Translation.Data) -> String?,
): Translations {
    return toDbTranslations(original, localeOrNull(originalLanguage, originalCountry), map)
}

fun TmdbTranslations?.toDbTranslations(
    original: String?,
    originalLocale: Locale?,
    map: (TmdbTranslations.Translation.Data) -> String?,
): Translations {
    val originalTranslation = when (original) {
        null -> null
        else -> Translation(
            locale = originalLocale,
            value = original,
        )
    }
    return Translations(
        translations = this?.translations.orEmpty()
            .mapNotNull {
                val data = it.data ?: return@mapNotNull null
                val locale = localeOrNull(it.iso_639_1, it.iso_3166_1) ?: return@mapNotNull null
                val translation = map(data)?.trim()
                if (!translation.isNullOrEmpty()) {
                    Translation(
                        locale = locale,
                        value = translation,
                    )
                } else {
                    null
                }
            }
            .plus(listOfNotNull(originalTranslation)),
        originalLocale = originalTranslation?.locale,
    )
}

fun TmdbVideoType.toVideoType() = when (this) {
    TRAILER -> VideoType.TRAILER
    TEASER -> VideoType.TEASER
    CLIP -> VideoType.CLIP
    FEATURETTE -> VideoType.FEATURETTE
    OPENING_CREDITS -> VideoType.OPENING_CREDITS
}

fun Images.toShowImages() = ShowImages(
    posters = this.posters.orEmpty().toImages(),
    backdrops = this.backdrops.orEmpty().toImages(),
    // TODO logos not exposed by library
    logos = emptyList(),
)

fun List<TmdbImage>.toImages(): List<Image> {
    return this.mapNotNull { it.toImage() }
}

fun TmdbImage.toImage(): Image? {
    return Image(
        width = width ?: return null,
        height = height ?: return null,
        language = if (iso_639_1 == null) null else Locale(iso_639_1),
        url = "https://image.tmdb.org/t/p/original$file_path",
        ratings = ImageRatings(
            tmdb = Rating.Tmdb(vote_average, vote_count?.toLong()),
        ),
    )
}

fun Videos.toVideos(): List<Video> {
    return this.results.orEmpty().mapIndexedNotNull { index, video -> video.toVideo(index) }
}

private fun Videos.Video.toVideo(index: Int): Video? {
    val provider = TmdbParser.videoProvider(site) ?: return null
    return Video(
        provider = provider,
        key = key ?: return null,
        type = (type ?: return null).toVideoType(),
        duration = null,
        language = iso_639_1, // TODO: fare meglio?
        date = null, // TODO: technically, the API has this info, it's the library that lacks it
        sortingWeight = index.toFloat(),
    )
}

fun TvShow.toShowInfo(): ShowInfo {
    val id = id!!.toLong()
    return ShowInfo(
        id = TmdbShowId(id).toExternalId(),
        name = translations.toDbTranslations(original_name, original_language, origin_country?.singleOrNull()) { it.name },
        externalIds = ShowExternalIds(
            tmdb = id,
            tvdb = external_ids?.tvdb_id?.toLong(),
            imdb = external_ids?.imdb_id,
        ),
        status = status?.let { TmdbParser.status(it) },
        ratings = ShowRatings(
            tmdb = Rating.Tmdb(vote_average, vote_count?.toLong()),
        ),
    )
}
