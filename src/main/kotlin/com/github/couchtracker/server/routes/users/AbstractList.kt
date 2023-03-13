package com.github.couchtracker.server.routes.users

import com.github.couchtracker.server.ApplicationData
import com.github.couchtracker.server.model.api.users.lists.AbstractPatchListBody
import com.github.couchtracker.server.model.db.UserDbo
import com.github.couchtracker.server.model.db.list.AbstractListDbo
import com.github.couchtracker.server.util.ktor.queryParams
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.resources.get
import io.ktor.server.resources.patch
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import org.litote.kmongo.Id
import java.util.Locale

interface AbstractList<PatchBody : AbstractPatchListBody> {
    fun userId(): Id<UserDbo>

    suspend fun getList(ad: ApplicationData): AbstractListDbo<*>?

    suspend fun patch(ad: ApplicationData, body: PatchBody): Boolean
}

inline fun <reified Id : AbstractList<PatchBody>, reified PatchBody : AbstractPatchListBody> Route.abstractList(ad: ApplicationData) {
    get<Id> { url ->
        checkSelf(url.userId())
        val locales: List<Locale> by call.queryParams

        val list = url.getList(ad)
        if (list == null) {
            call.respond(HttpStatusCode.NotFound)
        } else {
            call.respond(list.toApi(ad, locales))
        }
    }
    patch<Id> { url ->
        checkSelf(url.userId())
        val body = call.receive<PatchBody>()
        if (url.patch(ad, body)) {
            call.respond(HttpStatusCode.NoContent)
        } else {
            call.respond(HttpStatusCode.NotFound)
        }
    }
}
