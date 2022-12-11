package com.github.couchtracker.server.api.tmdb

import com.github.couchtracker.server.common.model.shows.Show
import com.github.couchtracker.server.api.ApiItem
import com.github.couchtracker.server.api.ShowApis
import com.github.couchtracker.server.common.cacheActor
import com.github.couchtracker.server.common.get
import com.github.couchtracker.server.common.makeNotNull
import com.github.couchtracker.server.common.model.ExternalId
import com.github.couchtracker.server.db.model.ShowDbo
import com.github.couchtracker.server.db.model.ShowVideos
import com.github.couchtracker.server.db.model.shows
import com.uwetrottmann.tmdb2.Tmdb
import com.uwetrottmann.tmdb2.entities.AppendToResponse
import com.uwetrottmann.tmdb2.entities.TvShow
import com.uwetrottmann.tmdb2.enumerations.AppendToResponseItem
import kotlinx.coroutines.CoroutineScope
import org.litote.kmongo.coroutine.CoroutineClient
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import retrofit2.await
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

class TmdbApisCache(val client: Tmdb, scope: CoroutineScope) : ShowApis<Int> {

    private val showCache = scope.cacheActor<Int, TvShow>(
        expireTimeForSuccess = 1.hours,
        expireTimeForFailures = 1.minutes,
    ) { id ->
        client
            .tvService()
            .tv(
                id, "eng", AppendToResponse(
                    AppendToResponseItem.ALTERNATIVE_TITLES,
                    AppendToResponseItem.CREDITS,
                    AppendToResponseItem.EXTERNAL_IDS,
                    AppendToResponseItem.IMAGES,
                    AppendToResponseItem.KEYWORDS,
                    AppendToResponseItem.RELEASE_DATES,
                    AppendToResponseItem.TRANSLATIONS,
                    AppendToResponseItem.VIDEOS,
                )
            )
            .makeNotNull()
            .await()
    }

    override val show = object : ApiItem<Int, Show>() {
        override suspend fun load(key: Int, db: CoroutineDatabase): Show? {
            return db
                .shows()
                .findOne(ShowDbo::id eq ExternalId("tmdb", key.toString()))
                ?.toApi()
        }

        override suspend fun download(key: Int): Show {
            return showCache.get(key).toShow()
        }
    }

    override val showVideos: ApiItem<Int, ShowVideos>
        get() = TODO("Not yet implemented")

    override suspend fun save(id: Int, client: CoroutineClient) {

    }
}