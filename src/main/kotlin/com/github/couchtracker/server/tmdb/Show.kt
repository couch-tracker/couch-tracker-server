package com.github.couchtracker.server.tmdb

import com.github.couchtracker.server.Config
import com.github.couchtracker.server.common.model.*
import com.github.couchtracker.server.db.model.ShowDbo
import com.uwetrottmann.tmdb2.entities.AppendToResponse
import com.uwetrottmann.tmdb2.enumerations.AppendToResponseItem

fun ShowDbo.Companion.downloadFromTmdb(tmdbId: Int): Result<ShowDbo> {
    val tmdb = Config.Tmdb.client() ?: return Result.failure(IllegalStateException("Cannot download from TMDB: API key not set"))

    // TODO try to use enqueue to make function suspend
    val result = tmdb.tvService()
        .tv(
            tmdbId, "eng", AppendToResponse(
                AppendToResponseItem.TRANSLATIONS,
                AppendToResponseItem.EXTERNAL_IDS
            )
        )
        .execute()

    if (!result.isSuccessful) {
        return Result.failure(Exception(result.message()))
    }

    val show = result.body()!!

    return Result.success(
        ShowDbo(
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
        ))
}