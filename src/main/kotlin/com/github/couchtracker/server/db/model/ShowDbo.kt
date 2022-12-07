package com.github.couchtracker.server.db.model

import com.github.couchtracker.server.api.model.Show
import com.github.couchtracker.server.common.model.*
import com.github.couchtracker.server.db.showOrderings
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.coroutine.projection
import org.litote.kmongo.eq

@Serializable
data class ShowDbo(
    @SerialName("_id")
    val id: ExternalId,
    val name: Translations,
    val externalIds: ShowExternalIds,

    val status: ShowStatus?,
    val ratings: ShowRatings?,
) {

    suspend fun toApi(db: CoroutineDatabase) = Show(
        id = this.id,
        name = this.name,
        externalIds = this.externalIds,
        status = this.status,
        ratings = this.ratings,
        orderings = db.showOrderings().projection(ShowOrderingDbo::id, ShowOrderingDbo::show eq this.id).toList()
    )
}
