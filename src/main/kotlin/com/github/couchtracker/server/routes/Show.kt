package com.github.couchtracker.server.routes

import com.github.couchtracker.server.ApplicationData
import com.github.couchtracker.server.common.tvApis
import com.github.couchtracker.server.model.externalIds.ExternalId
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
        @Resource("images")
        data class Images(val parent: Show)

        @Serializable
        @Resource("videos")
        data class Videos(val parent: Show)
    }
}


// TODO if passing "tmdb:asd" as ID, crashes with 500
fun Route.showRoutes(ad: ApplicationData) {
    get<Routes.Show> { url ->
        val showsApi = tvApis(ad, url.eid)

        val show = showsApi.show(url.eid).info.loadOrDownload(ad.connection)
        call.respond(show)
    }

    get<Routes.Show.Images> { url ->
        val showsApi = tvApis(ad, url.parent.eid)

        val images = showsApi.show(url.parent.eid).images.loadOrDownload(ad.connection)
        call.respond(images)
    }

    get<Routes.Show.Videos> { url ->
        val showsApi = tvApis(ad, url.parent.eid)

        val show = showsApi.show(url.parent.eid).videos.loadOrDownload(ad.connection)
        call.respond(show)
    }
}
