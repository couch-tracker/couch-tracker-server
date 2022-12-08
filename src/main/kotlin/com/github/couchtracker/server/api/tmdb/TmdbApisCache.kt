package com.github.couchtracker.server.api.tmdb

import com.github.couchtracker.server.common.model.BaseShow
import com.github.couchtracker.server.api.ApiCallCache
import com.github.couchtracker.server.api.ApiItem
import com.github.couchtracker.server.api.ShowApis
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
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import retrofit2.await

class TmdbApisCache(val client: Tmdb, scope: CoroutineScope) : ShowApis<Int> {

    private val showCache = ApiCallCache<Int, TvShow>(scope) { id ->
        client
            .tvService()
            .tv(
                id, "eng", AppendToResponse(
                    AppendToResponseItem.TRANSLATIONS,
                    AppendToResponseItem.EXTERNAL_IDS
                )
            )
            .makeNotNull()
            .await()
    }

    override val baseShow = object : ApiItem<Int, BaseShow>() {
        override suspend fun load(key: Int, db: CoroutineDatabase): BaseShow? {
            return db
                .shows()
                .findOne(ShowDbo::id eq ExternalId("tmdb", key.toString()))
                ?.toApi()
        }

        override suspend fun download(key: Int): BaseShow {
            return showCache.get(key).toShow()
        }
    }

    override val showVideos: ApiItem<Int, ShowVideos>
        get() = TODO("Not yet implemented")
}