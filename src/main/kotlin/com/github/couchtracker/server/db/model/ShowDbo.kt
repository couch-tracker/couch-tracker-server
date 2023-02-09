package com.github.couchtracker.server.db.model

import com.github.couchtracker.server.db.DboCompanion
import com.github.couchtracker.server.model.Translation
import com.github.couchtracker.server.model.Translations
import com.github.couchtracker.server.model.externalIds.ExternalId
import com.github.couchtracker.server.model.shows.Show
import com.github.couchtracker.server.model.shows.ShowExternalIds
import com.github.couchtracker.server.model.shows.ShowImages
import com.github.couchtracker.server.model.shows.ShowRatings
import com.github.couchtracker.server.model.shows.ShowStatus
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.div
import org.litote.kmongo.textIndex
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ShowDbo(
    @SerialName("_id")
    val id: ExternalId,
    val name: Translations,
    val externalIds: ShowExternalIds,
    val adult: Boolean,

    val status: ShowStatus?,
    val ratings: ShowRatings,
    val images: ShowImages<ImageDbo>,
) {

    fun toApi() = Show(
        id = this.id,
        name = this.name,
        externalIds = this.externalIds,
        status = this.status,
        ratings = this.ratings,
        // TODO put this somewhere
        //  orderings = db.showOrderings().projection(ShowOrderingDbo::id, ShowOrderingDbo::show eq this.id).toList()
    )

    companion object : DboCompanion<ShowDbo> {

        override fun collection(db: CoroutineDatabase) = db.getCollection<ShowDbo>("shows")

        override suspend fun CoroutineCollection<ShowDbo>.setup() {
            ensureIndex((ShowDbo::name / Translation::value).textIndex())
        }
    }
}

fun CoroutineDatabase.shows() = ShowDbo.collection(this)
