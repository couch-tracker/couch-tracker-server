package com.github.couchtracker.server.routes

import com.github.couchtracker.server.ApplicationData
import com.github.couchtracker.server.common.tvApis
import com.github.couchtracker.server.model.externalIds.ExternalId
import io.ktor.resources.Resource
import io.ktor.server.application.call
import io.ktor.server.resources.get
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import kotlinx.serialization.Serializable

@Serializable
@Resource("/shows")
private class ShowRoutes {

    @Serializable
    @Resource("{eid}")
    data class Show(val parent: ShowRoutes, val eid: ExternalId) {

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
    get<ShowRoutes.Show> { url ->
        val showsApi = tvApis(ad, url.eid)

        val show = showsApi.show(url.eid).info.loadOrDownload(ad.connection)
        call.respond(show)
    }

    get<ShowRoutes.Show.Images> { url ->
        val showsApi = tvApis(ad, url.parent.eid)

        val images = showsApi.show(url.parent.eid).images.loadOrDownload(ad.connection)
        call.respond(images)
    }

    get<ShowRoutes.Show.Videos> { url ->
        val showsApi = tvApis(ad, url.parent.eid)

        val show = showsApi.show(url.parent.eid).videos.loadOrDownload(ad.connection)
        call.respond(show)
    }
}
