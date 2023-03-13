package com.github.couchtracker.server.model.db.list

import com.github.couchtracker.server.ApplicationData
import com.github.couchtracker.server.model.api.users.lists.ApiList
import com.github.couchtracker.server.model.common.list.ListDisplayOptions
import com.github.couchtracker.server.model.db.UserDbo
import com.github.couchtracker.server.util.DboCompanion
import com.mongodb.reactivestreams.client.ClientSession
import org.litote.kmongo.Id
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import java.util.Locale
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ListDbo(
    @Contextual
    @SerialName("_id")
    val id: Id<ListDbo>,

    @Contextual
    override val user: Id<UserDbo>,
    val index: Int,
    val name: String,
    override val displayOptions: ListDisplayOptions,
    val items: List<ListItem>,
) : AbstractListDbo<ApiList> {

    override fun listItems() = items

    override suspend fun toApi(ad: ApplicationData, locales: List<Locale>): ApiList {
        return ApiList(
            id = id.toString(),
            name = name,
            displayOptions = displayOptions,
            items = getSortedApiItems(ad, locales),
        )
    }

    companion object : DboCompanion<ListDbo> {
        override fun collection(db: CoroutineDatabase): CoroutineCollection<ListDbo> = db.getCollection("lists")

        override suspend fun CoroutineCollection<ListDbo>.setup() {
            ensureUniqueIndex(ListDbo::user, ListDbo::index)
        }

        suspend fun getNextListIndex(db: CoroutineDatabase, session: ClientSession, user: Id<UserDbo>): Int {
            // Cannot use projection because there is no projection(session, ...) in extension function in coroutine module
            // See: https://github.com/Litote/kmongo/issues/400
            return collection(db)
                .find(session, ListDbo::user eq user)
                .descendingSort(ListDbo::index)
                .limit(1)
                .first()
                ?.index
                ?.plus(1)
                ?: 0
        }
    }
}

suspend fun List<ListDbo>.toApi(ad: ApplicationData, locales: List<Locale>) = this
    .sortedBy { it.index }
    .map { it.toApi(ad, locales) }
