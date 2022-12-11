package com.github.couchtracker.server.infoProviders.tmdb

import com.github.couchtracker.server.common.model.shows.Show
import com.github.couchtracker.server.infoProviders.ApiItem
import com.github.couchtracker.server.infoProviders.TvApis
import com.github.couchtracker.server.common.cacheActor
import com.github.couchtracker.server.common.get
import com.github.couchtracker.server.common.makeNotNull
import com.github.couchtracker.server.common.model.ExternalId
import com.github.couchtracker.server.common.model.Video
import com.github.couchtracker.server.db.model.ShowDbo
import com.github.couchtracker.server.db.model.shows
import com.github.couchtracker.server.infoProviders.ShowApis
import com.uwetrottmann.tmdb2.Tmdb as TmdbClient
import com.uwetrottmann.tmdb2.entities.AppendToResponse
import com.uwetrottmann.tmdb2.entities.TvShow
import com.uwetrottmann.tmdb2.enumerations.AppendToResponseItem
import kotlinx.coroutines.CoroutineScope
import org.litote.kmongo.coroutine.CoroutineDatabase
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
            id.value, "eng", AppendToResponse(
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