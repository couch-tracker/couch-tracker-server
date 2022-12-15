package com.github.couchtracker.server.infoProviders.tmdb

import com.github.couchtracker.server.model.shows.Show
import com.github.couchtracker.server.infoProviders.ApiItem
import com.github.couchtracker.server.infoProviders.TvApis
import com.github.couchtracker.server.common.cacheActor
import com.github.couchtracker.server.common.get
import com.github.couchtracker.server.common.makeNotNull
import com.github.couchtracker.server.model.Video
import com.github.couchtracker.server.db.model.ShowDbo
import com.github.couchtracker.server.db.model.shows
import com.github.couchtracker.server.infoProviders.ShowApis
import com.github.couchtracker.server.model.Image
import com.github.couchtracker.server.model.shows.ShowImages
import com.uwetrottmann.tmdb2.Tmdb as TmdbClient
import com.uwetrottmann.tmdb2.entities.AppendToResponse
import com.uwetrottmann.tmdb2.entities.TvShow
import com.uwetrottmann.tmdb2.enumerations.AppendToResponseItem
import kotlinx.coroutines.CoroutineScope
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.coroutine.projection
import org.litote.kmongo.eq
import retrofit2.await
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

class TmdbTvApis(val client: TmdbClient, scope: CoroutineScope) : TvApis<TmdbShowId> {

    private val showCache = scope.cacheActor<TmdbShowId, TvShow>(
        expireTimeForSuccess = 1.hours,
        expireTimeForFailures = 1.minutes,
    ) { id ->
        client.tvService().tv(
            // TODO APIs seem not to respect language
            id.value, "en,null", AppendToResponse(
                AppendToResponseItem.ALTERNATIVE_TITLES,
                AppendToResponseItem.CREDITS,
                AppendToResponseItem.EXTERNAL_IDS,
                AppendToResponseItem.IMAGES,
                AppendToResponseItem.KEYWORDS,
                AppendToResponseItem.RELEASE_DATES,
                AppendToResponseItem.TRANSLATIONS,
                AppendToResponseItem.VIDEOS,
            )
        ).makeNotNull().await()
    }

    override fun show(id: TmdbShowId) = object : ShowApis {

        override val info = object : ApiItem<Show>() {
            override suspend fun load(db: CoroutineDatabase): Show? {
                return db.shows()
                    .findOne(ShowDbo::id eq id.toExternalId())
                    ?.toApi()
            }

            override suspend fun download(): Show {
                return showCache.get(id).toShow()
            }
        }

        override val images = object : ApiItem<ShowImages<Image>>() {
            override suspend fun load(db: CoroutineDatabase): ShowImages<Image>? {
                return db.shows()
                    .projection(ShowDbo::images, ShowDbo::id eq id.toExternalId())
                    .first()
                    ?.map { it.toApi() }
            }

            override suspend fun download(): ShowImages<Image> {
                return showCache.get(id).images.toShowImages()
            }

        }

        override val videos = object : ApiItem<List<Video>>() {
            override suspend fun load(db: CoroutineDatabase): List<Video>? {
                return null //TODO
            }

            override suspend fun download(): List<Video> {
                return showCache.get(id).videos.toVideos()
            }
        }
    }

}