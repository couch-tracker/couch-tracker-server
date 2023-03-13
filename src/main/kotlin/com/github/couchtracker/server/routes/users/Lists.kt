@file:UseSerializers(WrappedObjectIdSerializer::class)

package com.github.couchtracker.server.routes.users

import com.github.couchtracker.server.ApplicationData
import com.github.couchtracker.server.model.api.users.lists.CreateListBody
import com.github.couchtracker.server.model.api.users.lists.PatchListBody
import com.github.couchtracker.server.model.common.list.ListDisplayOptions
import com.github.couchtracker.server.model.db.list.ListDbo
import com.github.couchtracker.server.model.db.list.toApi
import com.github.couchtracker.server.util.ktor.queryParams
import com.github.couchtracker.server.util.serializers.WrappedObjectIdSerializer
import com.github.couchtracker.server.util.setOptionals
import com.github.couchtracker.server.util.setTo
import com.github.couchtracker.server.util.transaction
import io.ktor.http.HttpStatusCode
import io.ktor.resources.Resource
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.resources.delete
import io.ktor.server.resources.get
import io.ktor.server.resources.post
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import org.litote.kmongo.eq
import org.litote.kmongo.id.WrappedObjectId
import org.litote.kmongo.newId
import java.util.Locale
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
@Resource("lists")
private data class Lists(val user: Users.Id) {

    @Serializable
    @Resource("{listId}")
    data class Id(val lists: Lists, val listId: WrappedObjectId<ListDbo>) : AbstractList<PatchListBody> {

        override fun userId() = lists.user.userId

        override suspend fun getList(ad: ApplicationData): ListDbo? {
            return ListDbo.collection(ad.db).findOne(
                ListDbo::user eq lists.user.userId,
                ListDbo::id eq listId,
            )
        }

        override suspend fun patch(ad: ApplicationData, body: PatchListBody): Boolean {
            val list = getList(ad) ?: return false

            val updateBson = setOptionals(
                ListDbo::name setTo body.name,
                ListDbo::items setTo body.patchedItems(list.items),
                ListDbo::displayOptions setTo body.displayOptions(list.displayOptions),
            )
            if (updateBson != null) {
                ListDbo.collection(ad.db).updateOne(
                    filter = ListDbo::id eq list.id,
                    update = updateBson,
                )
            }
            return true
        }
    }
}

fun Route.lists(ad: ApplicationData) {
    get<Lists> { url ->
        val locales: List<Locale> by call.queryParams
        val user = checkSelf(url.user.userId)
        val lists = ListDbo.collection(ad.db).find(
            ListDbo::user eq user.id,
        ).toList()

        call.respond(lists.toApi(ad, locales))
    }

    post<Lists> { url ->
        val user = checkSelf(url.user.userId)
        val body = call.receive<CreateListBody>()

        ad.mongoClient.transaction { session ->
            ListDbo.collection(ad.db).insertOne(
                session,
                ListDbo(
                    id = newId(),
                    user = user.id,
                    name = body.name.value,
                    items = emptyList(),
                    displayOptions = ListDisplayOptions.DEFAULT,
                    index = ListDbo.getNextListIndex(ad.db, session, user.id),
                ),
            )
        }

        call.respond(HttpStatusCode.Created)
    }

    abstractList<Lists.Id, PatchListBody>(ad)

    delete<Lists.Id> { url ->
        val user = checkSelf(url.userId())
        val found = ListDbo.collection(ad.db).findOne(
            ListDbo::id eq url.listId,
            ListDbo::user eq user.id,
        )
        println(found)
        val deleted = ListDbo.collection(ad.db).deleteOne(
            ListDbo::id eq url.listId,
            ListDbo::user eq user.id,
        ).deletedCount > 0
        if (deleted) {
            call.respond(HttpStatusCode.NoContent)
        } else {
            call.respond(HttpStatusCode.NotFound)
        }
    }
}
