package com.github.couchtracker.server.db.model

import com.github.couchtracker.server.db.DboCompanion
import kotlinx.serialization.SerialName
import org.litote.kmongo.Id
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.coroutine.CoroutineDatabase

data class UserDbo(
    @SerialName("_id")
    val id : Id<UserDbo>,

    val username: String,
    val email: String,
    val password: String,
    val name: String,
) {

    companion object : DboCompanion<UserDbo> {

        override fun collection(db: CoroutineDatabase) = db.getCollection<UserDbo>("users")

        override suspend fun CoroutineCollection<UserDbo>.setup() {
            ensureUniqueIndex(UserDbo::username)
            ensureUniqueIndex(UserDbo::email)
        }
    }
}

fun CoroutineDatabase.users() = UserDbo.collection(this)