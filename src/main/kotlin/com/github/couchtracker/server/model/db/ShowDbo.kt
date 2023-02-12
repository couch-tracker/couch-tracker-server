package com.github.couchtracker.server.model.db

import com.github.couchtracker.server.model.common.ShowExternalIds
import com.github.couchtracker.server.model.common.ShowImages
import com.github.couchtracker.server.model.common.ShowRatings
import com.github.couchtracker.server.model.common.ShowStatus
import com.github.couchtracker.server.model.common.Translations
import com.github.couchtracker.server.model.common.externalIds.ExternalId
import com.github.couchtracker.server.model.infoProviders.ShowInfo
import com.github.couchtracker.server.util.DboCompanion
import org.litote.kmongo.coroutine.CoroutineDatabase
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
    val images: ShowImages,
) {

    fun toShowInfo() = ShowInfo(
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
    }
}

fun CoroutineDatabase.shows() = ShowDbo.collection(this)
