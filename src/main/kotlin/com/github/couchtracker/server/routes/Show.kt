package com.github.couchtracker.server.routes

import com.github.couchtracker.server.ApplicationData
import com.github.couchtracker.server.common.getInfoProvider
import com.github.couchtracker.server.common.validate
import com.github.couchtracker.server.common.model.ExternalId
import com.github.couchtracker.server.common.tvApis
import io.ktor.http.*
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

@Serializable
@Resource("/shows")
private class Routes {

    @Serializable
    @Resource("{eid}")
    data class Show(val parent: Routes, val eid: ExternalId) {

        @Serializable
        @Resource("videos")
        data class Videos(val parent: Show)
    }
}


// TODO if passing "tmdb:asd" as ID, crashes with 500
fun Route.showRoutes(ad: ApplicationData) {
    get<Routes.Show> { url ->
        val showsApi = tvApis(ad, url.eid.provider)

        val show = showsApi.show(url.eid.id).info.loadOrDownload(ad.connection)
        call.respond(show)
    }

    get<Routes.Show.Videos> { url ->
        val showsApi = tvApis(ad, url.parent.eid.provider)

        val show = showsApi.show(url.parent.eid.id).videos.loadOrDownload(ad.connection)
        call.respond(show)
    }
}
