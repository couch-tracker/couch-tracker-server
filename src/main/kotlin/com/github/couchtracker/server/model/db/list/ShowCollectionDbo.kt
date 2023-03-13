package com.github.couchtracker.server.model.db.list

import com.github.couchtracker.server.ApplicationData
import com.github.couchtracker.server.model.api.users.lists.ApiListItem
import com.github.couchtracker.server.model.api.users.lists.ApiShowCollection
import com.github.couchtracker.server.model.common.list.ListDisplayOptions
import com.github.couchtracker.server.model.db.UserDbo
import com.github.couchtracker.server.util.DboCompanion
import org.litote.kmongo.Id
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.coroutine.CoroutineDatabase
import java.util.Locale
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class ShowCollectionDbo(
    @Contextual
    override val user: Id<UserDbo>,
    override val displayOptions: ListDisplayOptions,
    val shows: List<ListItem.Show>,
) : AbstractListDbo<ApiShowCollection> {

    override fun listItems() = shows

    override suspend fun toApi(ad: ApplicationData, locales: List<Locale>) = ApiShowCollection(
        displayOptions = displayOptions,
        shows = getSortedApiItems(ad, locales).filterIsInstance<ApiListItem.Show>(),
    )

    companion object : DboCompanion<ShowCollectionDbo> {
        override fun collection(db: CoroutineDatabase): CoroutineCollection<ShowCollectionDbo> {
            return db.getCollection("show-collections")
        }

        override suspend fun CoroutineCollection<ShowCollectionDbo>.setup() {
            ensureUniqueIndex(ShowCollectionDbo::user)
        }
    }
}
