package com.github.couchtracker.server.model.db

import com.github.couchtracker.server.ApplicationData
import com.github.couchtracker.server.model.api.users.ApiUser
import com.github.couchtracker.server.util.DboCompanion
import com.github.couchtracker.server.util.Email
import com.github.couchtracker.server.util.Password
import com.github.couchtracker.server.util.Username
import com.github.couchtracker.server.util.insertIgnoreDuplicate
import org.litote.kmongo.Id
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.newId
import kotlinx.datetime.Instant
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserDbo(
    @Contextual
    @SerialName("_id")
    val id: Id<UserDbo>,

    // User info
    val username: String,
    val email: String,
    val password: String,
    val name: String,

    // Security info
    @Contextual
    val invalidateTokensAfter: Instant? = null,
) {

    fun toApi() = ApiUser(
        id = id.toString(),
        username = Username(username),
        email = Email(email),
        name = name,
    )

    companion object : DboCompanion<UserDbo> {

        override fun collection(db: CoroutineDatabase) = db.getCollection<UserDbo>("users")

        override suspend fun CoroutineCollection<UserDbo>.setup() {
            ensureUniqueIndex(UserDbo::username)
            ensureUniqueIndex(UserDbo::email)
        }

        suspend fun insert(
            applicationData: ApplicationData,
            username: Username,
            email: Email,
            password: Password,
            name: String,
        ): Boolean {
            val hashedPassword = applicationData.config.argon2.hash(password)
            val user = UserDbo(
                id = newId(),
                username = username.value,
                email = email.value,
                password = hashedPassword,
                name = name,
            )

            return insertIgnoreDuplicate {
                collection(applicationData.db).insertOne(user)
            }
        }
    }
}

fun CoroutineDatabase.users() = UserDbo.collection(this)
