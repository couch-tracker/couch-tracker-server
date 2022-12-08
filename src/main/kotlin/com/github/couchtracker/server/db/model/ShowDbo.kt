package com.github.couchtracker.server.db.model

import com.github.couchtracker.server.common.model.BaseShow
import com.github.couchtracker.server.common.model.*
import com.github.couchtracker.server.db.DboCompanion
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.div
import org.litote.kmongo.textIndex

@Serializable
data class ShowDbo(
    @SerialName("_id")
    val id: ExternalId,
    val name: Translations,
    val externalIds: ShowExternalIds,

    val status: ShowStatus?,
    val ratings: ShowRatings,
    val videos: ShowVideos,
) {

    fun toApi() = BaseShow(
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

typealias ShowVideos = List<ShowVideo>

@Serializable
data class ShowVideo(
    val url : String
)

fun CoroutineDatabase.shows() = ShowDbo.collection(this)


