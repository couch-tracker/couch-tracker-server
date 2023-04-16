package com.github.couchtracker.server.infoProviders.tmdb

import com.github.couchtracker.server.infoProviders.ApiItem
import com.github.couchtracker.server.infoProviders.ShowApis
import com.github.couchtracker.server.infoProviders.TvApis
import com.github.couchtracker.server.infoProviders.ids.TmdbShowId
import com.github.couchtracker.server.model.common.ShowImages
import com.github.couchtracker.server.model.common.Video
import com.github.couchtracker.server.model.db.ShowDbo
import com.github.couchtracker.server.model.db.shows
import com.github.couchtracker.server.model.infoProviders.ShowInfo
import com.github.couchtracker.server.util.asNotNull
import com.github.couchtracker.server.util.cacheActor
import com.github.couchtracker.server.util.get
import com.uwetrottmann.tmdb2.entities.AppendToResponse
import com.uwetrottmann.tmdb2.entities.TvShow
import com.uwetrottmann.tmdb2.enumerations.AppendToResponseItem
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.coroutine.projection
import org.litote.kmongo.eq
import retrofit2.await
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlinx.coroutines.CoroutineScope
import com.uwetrottmann.tmdb2.Tmdb as TmdbClient

class TmdbTvApis(val client: TmdbClient, val tmdb: Tmdb, scope: CoroutineScope) : TvApis<TmdbShowId> {

    private val showCache = scope.cacheActor<TmdbShowId, TvShow>(
        expireTimeForSuccess = 1.hours,
        expireTimeForFailures = 1.minutes,
    ) { id ->
        client.tvService().tv(
            id.value.toBigInteger().intValueExact(),
            "",
            AppendToResponse(
                AppendToResponseItem.ALTERNATIVE_TITLES,
                AppendToResponseItem.CREDITS,
                AppendToResponseItem.EXTERNAL_IDS,
                AppendToResponseItem.IMAGES,
                AppendToResponseItem.KEYWORDS,
                AppendToResponseItem.RELEASE_DATES,
                AppendToResponseItem.TRANSLATIONS,
                AppendToResponseItem.VIDEOS,
            ),
        ).asNotNull().await()
    }

    override fun show(id: TmdbShowId) = object : ShowApis {

        override val info = object : ApiItem<ShowInfo>() {
            override suspend fun load(db: CoroutineDatabase): ShowInfo? {
                return db.shows()
                    .findOne(ShowDbo::id eq id.toExternalId())
                    ?.toShowInfo()
            }

            override suspend fun download(): ShowInfo {
                val config = tmdb.configuration()
                return showCache.get(id).toShowInfo(config)
            }
        }

        override val images = object : ApiItem<ShowImages>() {
            override suspend fun load(db: CoroutineDatabase): ShowImages? {
                return db.shows()
                    .projection(ShowDbo::images, ShowDbo::id eq id.toExternalId())
                    .first()
            }

            override suspend fun download(): ShowImages {
                val show = showCache.get(id)
                val config = tmdb.configuration()
                return show.images?.toShowImages(config.images, show.originalLocale()) ?: ShowImages.EMPTY
            }
        }

        override val videos = object : ApiItem<List<Video>>() {
            override suspend fun load(db: CoroutineDatabase): List<Video>? {
                return null // TODO
            }

            override suspend fun download(): List<Video> {
                return showCache.get(id).videos.toVideos()
            }
        }
    }
}
