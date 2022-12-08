package com.github.couchtracker.server.api

import com.github.couchtracker.server.common.model.BaseShow
import com.github.couchtracker.server.db.model.ShowVideos

interface ShowApis<ID: Any> {

    val baseShow : ApiItem<ID, BaseShow>
    val showVideos : ApiItem<ID, ShowVideos>
}