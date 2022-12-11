package com.github.couchtracker.server.api

import com.github.couchtracker.server.common.model.shows.Show
import com.github.couchtracker.server.db.model.ShowVideos
import org.litote.kmongo.coroutine.CoroutineClient

interface ShowApis<ID: Any> {

    val show : ApiItem<ID, Show>
    val showVideos : ApiItem<ID, ShowVideos>

    suspend fun save(id: ID, client: CoroutineClient)
}