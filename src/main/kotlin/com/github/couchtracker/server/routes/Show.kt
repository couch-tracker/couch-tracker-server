package com.github.couchtracker.server.routes

import com.github.couchtracker.server.ApplicationData
import com.github.couchtracker.server.common.validate
import com.github.couchtracker.server.common.model.ExternalId
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

        init {
            require(eid.provider == "tmdb")
        }

        @Serializable
        @Resource("videos")
        data class Videos(val parent: Show)
    }
}


fun Route.showRoutes(ad: ApplicationData) {
    get<Routes.Show> {url ->
        validate( ad.tmdbApis != null) {
            respond(HttpStatusCode.NotImplemented,"This server doesn't support TMDB.")
        }

        val show = ad.tmdbApis.show.loadOrDownload(url.eid.id.toInt(), ad.connection)
        call.respond(show)
    }

    get<Routes.Show.Videos> {url->
        call.respond("No videos for ${url.parent.eid}")
    }
}
