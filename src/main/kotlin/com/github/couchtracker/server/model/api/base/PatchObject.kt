package com.github.couchtracker.server.model.api.base

import com.github.couchtracker.server.util.DboCompanion
import org.bson.conversions.Bson
import org.litote.kmongo.Id
import org.litote.kmongo.coroutine.CoroutineDatabase

interface PatchObject<T : Any> {

    val dbo: DboCompanion<T>

    fun updateBson(): Bson?

    suspend fun update(db: CoroutineDatabase, id: Id<T>): UpdateResult {
        val bson = updateBson()
        return when {
            bson != null -> {
                this.dbo.collection(db).updateOneById(
                    id = id,
                    update = bson,
                )
                UpdateResult.UPDATED
            }
            else -> UpdateResult.NO_PARAMS
        }
    }
}

enum class UpdateResult {
    UPDATED, NO_PARAMS
}
