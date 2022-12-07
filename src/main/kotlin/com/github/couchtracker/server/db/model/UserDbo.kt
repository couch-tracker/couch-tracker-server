package com.github.couchtracker.server.db.model

import kotlinx.serialization.SerialName
import org.litote.kmongo.Id
import org.litote.kmongo.coroutine.CoroutineDatabase

data class UserDbo(
    @SerialName("_id")
    val id : Id<UserDbo>,

    val username: String,
    val email: String,
    val password: String,
    val name: String,
) {

    companion object {
        suspend fun setup(db: CoroutineDatabase) {
            db.users().apply {
                ensureUniqueIndex(UserDbo::username)
                ensureUniqueIndex(UserDbo::email)
            }
        }
    }
}

fun CoroutineDatabase.users() = this.getCollection<UserDbo>("users")