package com.github.couchtracker.server.routes

import com.github.couchtracker.server.ApplicationData
import com.github.couchtracker.server.JWT
import com.github.couchtracker.server.accessPrincipal
import com.github.couchtracker.server.common.log
import com.github.couchtracker.server.common.Password
import com.github.couchtracker.server.common.validate
import com.github.couchtracker.server.db.model.UserDbo
import io.ktor.http.*
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.Route
import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable
import org.litote.kmongo.*

@Serializable
@Resource("/auth")
private class AuthRoutes {

    @Serializable
    @Resource("login")
    data class Login(val parent: AuthRoutes) {
        @Serializable
        data class Body(val login: String, val password: Password)
    }

    @Serializable
    @Resource("change-password")
    data class ChangePassword(val parent: AuthRoutes) {
        @Serializable
        data class Body(val oldPassword: Password, val newPassword: Password, val invalidateOldLogins : Boolean)
    }

    @Serializable
    @Resource("refresh")
    data class Refresh(val parent: AuthRoutes)
}

fun Route.authRoutes(ad: ApplicationData) {
    post<AuthRoutes.Login> {
        val (login, password) = call.receive<AuthRoutes.Login.Body>()

        val user = UserDbo.collection(ad.connection).findOne(or(UserDbo::email eq login, UserDbo::username eq login))
        val passwordCorrect = ad.config.argon2.verify(user?.password, password)

        if (user != null && passwordCorrect) {
            if (ad.config.argon2.needsRehash(user.password)) {
                log.info { "Rehashing ${user.email}'s password..." }
                UserDbo.collection(ad.connection).updateOne(
                    filter = UserDbo::id eq user.id,
                    update = setValue(UserDbo::password, ad.config.argon2.hash(password))
                )
            }
            call.respond(JWT.Login.generate(ad, user))
        } else {
            call.respond(HttpStatusCode.Unauthorized.description("Invalid login and/or password"))
        }
    }

    authenticate(JWT.Login.ACCESS) {
        post<AuthRoutes.ChangePassword> {
            val (oldPassword, newPassword, invalidateOldLogins) = call.receive<AuthRoutes.ChangePassword.Body>()
            validate(newPassword.validate()) {
                respond(HttpStatusCode.BadRequest.description("New password isn't strong enough"))
            }

            val user = call.accessPrincipal.user

            if(!ad.config.argon2.verify(user.password, oldPassword)) {
                call.respond(HttpStatusCode.Unauthorized.description("Old password is incorrect"))
            }

            val hashedPassword = ad.config.argon2.hash(newPassword)
            UserDbo.collection(ad.connection).updateOneById(
                id = user.id,
                update = set(
                    UserDbo::password setTo hashedPassword,
                    UserDbo::invalidateTokensAfter setTo (if(invalidateOldLogins) Clock.System.now() else null),
                ),
                updateOnlyNotNullProperties = true,
            )

            call.respond(JWT.Login.generate(ad, user))
        }
    }

    authenticate(JWT.Login.REFRESH) {
        post<AuthRoutes.Refresh> {
            // TODO: add mechanism to invalidate old refresh token

            val user = call.principal<JWT.Login.RefreshPrincipal>()!!.user
            call.respond(JWT.Login.generate(ad, user))
        }
    }
}

