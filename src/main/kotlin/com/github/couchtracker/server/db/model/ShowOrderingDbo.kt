package com.github.couchtracker.server.db.model

import com.github.couchtracker.server.model.ExternalId
import com.github.couchtracker.server.model.Translations
import com.github.couchtracker.server.db.DboCompanion
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.litote.kmongo.coroutine.CoroutineDatabase

@Serializable
data class ShowOrderingDbo(
    @SerialName("_id")
    val id : ExternalId,
    val show : ExternalId,
    val name: Translations,
    val description: Translations,
) {

    companion object : DboCompanion<ShowOrderingDbo> {

        override fun collection(db: CoroutineDatabase) = db.getCollection<ShowOrderingDbo>("showOrderings")
    }
}

fun CoroutineDatabase.showOrderings() = ShowOrderingDbo.collection(this)

