package com.github.couchtracker.server.routes

import com.github.couchtracker.server.ApplicationData
import com.github.couchtracker.server.JWT
import com.github.couchtracker.server.common.constantTime
import com.github.couchtracker.server.common.log
import com.github.couchtracker.server.common.serializers.Password
import com.github.couchtracker.server.db.model.UserDbo
import io.ktor.http.*
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.Route
import kotlinx.serialization.Serializable
import org.litote.kmongo.eq
import org.litote.kmongo.or
import org.litote.kmongo.setValue
import kotlin.math.log
import kotlin.time.Duration.Companion.milliseconds

@Serializable
@Resource("/auth")
private class AuthRoutes {

    @Serializable
    @Resource("login")
    data class Login(val parent: AuthRoutes) {
        @Serializable
        data class Data(val login: String, val password: Password)
    }
}

fun Route.authRoutes(ad: ApplicationData) {
    post<AuthRoutes.Login> {
        val (login, password) = call.receive<AuthRoutes.Login.Data>()

        val tokens = constantTime(ad.config.argon2.maxDuration + 500.milliseconds) {
            val user = UserDbo.collection(ad.connection).findOne(or(UserDbo::email eq login, UserDbo::username eq login))

            if (user != null && ad.config.argon2.verify(user.password, password)) {
                // We've verified that the user exists and has the correct password
                // In this case we're not worried about timing attacks, so we can cancel the constant time
                // This has the effect to speed up the login by about 500ms for people with correct login AND password
                cancel()
                if (ad.config.argon2.needsRehash(user.password)) {
                    log.info { "Rehashing ${user.email}'s password..." }
                    UserDbo.collection(ad.connection).updateOne(
                        filter = UserDbo::id eq user.id,
                        update = setValue(UserDbo::password, ad.config.argon2.hash(password))
                    )
                }
                JWT.Login.generate(ad, user)
            } else null
        }
        if (tokens != null) call.respond(tokens)
        else call.respond(HttpStatusCode.Unauthorized.description("Invalid login and/or password"))
    }
}

