package com.github.couchtracker.server.routes.users

import com.github.couchtracker.server.ApplicationData
import com.github.couchtracker.server.model.api.users.lists.PatchShowCollectionBody
import com.github.couchtracker.server.model.common.list.ListDisplayOptions
import com.github.couchtracker.server.model.db.list.ShowCollectionDbo
import com.github.couchtracker.server.util.setOptionals
import com.github.couchtracker.server.util.setTo
import com.github.couchtracker.server.util.transaction
import io.ktor.resources.Resource
import io.ktor.server.routing.Route
import org.litote.kmongo.eq
import kotlinx.serialization.Serializable

@Serializable
@Resource("show-collection")
private class ShowCollection(val user: Users.Id) : AbstractList<PatchShowCollectionBody> {

    override fun userId() = user.userId

    override suspend fun getList(ad: ApplicationData): ShowCollectionDbo {
        val collection = ShowCollectionDbo.collection(ad.db)

        return ad.mongoClient.transaction { session ->
            val showCollection = collection.findOne(session, ShowCollectionDbo::user eq user.userId)
            if (showCollection != null) {
                showCollection
            } else {
                val created = ShowCollectionDbo(
                    user = user.userId,
                    displayOptions = ListDisplayOptions.DEFAULT,
                    shows = emptyList(),
                )
                collection.insertOne(session, created)
                created
            }
        }
    }

    override suspend fun patch(ad: ApplicationData, body: PatchShowCollectionBody): Boolean {
        val list = getList(ad)

        val updateBson = setOptionals(
            ShowCollectionDbo::shows setTo body.patchedItems(list.shows),
            ShowCollectionDbo::displayOptions setTo body.displayOptions.patched(list.displayOptions),
        )
        if (updateBson != null) {
            ShowCollectionDbo.collection(ad.db).updateOne(
                filter = ShowCollectionDbo::user eq user.userId,
                update = updateBson,
            )
        }
        return true
    }
}

fun Route.showCollection(ad: ApplicationData) {
    abstractList<ShowCollection, PatchShowCollectionBody>(ad)
}
