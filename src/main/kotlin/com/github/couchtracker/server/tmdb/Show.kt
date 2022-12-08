package com.github.couchtracker.server.tmdb

import com.github.couchtracker.server.Config
import com.github.couchtracker.server.common.makeNotNull
import com.github.couchtracker.server.common.model.*
import com.github.couchtracker.server.db.model.ShowDbo
import com.uwetrottmann.tmdb2.entities.AppendToResponse
import com.uwetrottmann.tmdb2.enumerations.AppendToResponseItem
import retrofit2.await

suspend fun ShowDbo.Companion.downloadFromTmdb(tmdbId: Int): ShowDbo {
    val tmdb = Config.Tmdb.client() ?: throw IllegalStateException("Cannot download from TMDB: API key not set")

    val show = tmdb.tvService()
        .tv(
            tmdbId, "eng", AppendToResponse(
                AppendToResponseItem.TRANSLATIONS,
                AppendToResponseItem.EXTERNAL_IDS
            )
        ).makeNotNull()
        .await()

    return ShowDbo(
        id = TmdbId(tmdbId),
        name = show.translations.toDbTranslations { it.name },
        externalIds = ShowExternalIds(
            tmdb = tmdbId.toLong(),
            tvdb = show.external_ids.tvdb_id.toLong(),
            imdb = show.external_ids.imdb_id,
        ),

        status = show.status?.let { ShowStatus.fromTmdbStatus(it) },
        ratings = ShowRatings(
            tmdb = Rating.Tmdb(show.vote_average, show.vote_count.toLong())
        ),
    )
}