package com.github.couchtracker.server.routes

import com.github.couchtracker.server.ApplicationData
import com.github.couchtracker.server.JWT
import com.github.couchtracker.server.accessPrincipal
import com.github.couchtracker.server.common.Password
import com.github.couchtracker.server.common.insertIgnoreDuplicate
import com.github.couchtracker.server.common.log
import com.github.couchtracker.server.common.validate
import com.github.couchtracker.server.config.SignupConfig
import com.github.couchtracker.server.db.model.UserDbo
import io.ktor.http.HttpStatusCode
import io.ktor.resources.Resource
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.resources.post
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import org.litote.kmongo.eq
import org.litote.kmongo.newId
import org.litote.kmongo.or
import org.litote.kmongo.set
import org.litote.kmongo.setTo
import org.litote.kmongo.setValue
import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable

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
        data class Body(val oldPassword: Password, val newPassword: Password, val invalidateOldLogins: Boolean)
    }

    @Serializable
    @Resource("refresh")
    data class Refresh(val parent: AuthRoutes)

    @Serializable
    @Resource("signup")
    data class SignUp(val parent: AuthRoutes) {
        @Serializable
        data class Body(
            val username: String,
            val email: String,
            val password: Password,
            val name: String,
        )
    }
}

fun Route.authRoutes(ad: ApplicationData) {
    login(ad)

    authenticate(JWT.Login.ACCESS) {
        changePassword(ad)
    }

    authenticate(JWT.Login.REFRESH) {
        refresh(ad)
    }

    signUp(ad)
}

private fun Route.refresh(ad: ApplicationData) = post<AuthRoutes.Refresh> {
    // TODO: add mechanism to invalidate old refresh token

    val user = call.principal<JWT.Login.RefreshPrincipal>()?.user ?: error("")
    call.respond(JWT.Login.generate(ad, user))
}

private fun Route.changePassword(ad: ApplicationData) = post<AuthRoutes.ChangePassword> {
    val (oldPassword, newPassword, invalidateOldLogins) = call.receive<AuthRoutes.ChangePassword.Body>()
    validate(newPassword.validate()) { "New password isn't strong enough" }

    val user = call.accessPrincipal.user

    if (!ad.config.argon2.verify(user.password, oldPassword)) {
        call.respond(HttpStatusCode.Unauthorized.description("Old password is incorrect"))
    }

    val hashedPassword = ad.config.argon2.hash(newPassword)
    UserDbo.collection(ad.connection).updateOneById(
        id = user.id,
        update = set(
            UserDbo::password setTo hashedPassword,
            UserDbo::invalidateTokensAfter setTo (if (invalidateOldLogins) Clock.System.now() else null),
        ),
        updateOnlyNotNullProperties = true,
    )

    call.respond(JWT.Login.generate(ad, user))
}

private fun Route.login(ad: ApplicationData) = post<AuthRoutes.Login> {
    val (login, password) = call.receive<AuthRoutes.Login.Body>()

    val user = UserDbo.collection(ad.connection).findOne(or(UserDbo::email eq login, UserDbo::username eq login))
    val passwordCorrect = ad.config.argon2.verify(user?.password, password)

    if (user != null && passwordCorrect) {
        if (ad.config.argon2.needsRehash(user.password)) {
            log.info { "Rehashing ${user.email}'s password..." }
            UserDbo.collection(ad.connection).updateOne(
                filter = UserDbo::id eq user.id,
                update = setValue(UserDbo::password, ad.config.argon2.hash(password)),
            )
        }
        call.respond(JWT.Login.generate(ad, user))
    } else {
        call.respond(HttpStatusCode.Unauthorized.description("Invalid login and/or password"))
    }
}

private fun Route.signUp(ad: ApplicationData) = post<AuthRoutes.SignUp> {
    val canSignUp = when (ad.config.signup) {
        is SignupConfig.Open -> true
        is SignupConfig.Closed -> false
    }
    validate(canSignUp, HttpStatusCode.Forbidden)

    val body = call.receive<AuthRoutes.SignUp.Body>()

    validate(!body.username.contains('@')) { "Username cannot contain @ sign" }
    validate(body.email.contains('@')) { "Email must contain @ sign" }
    validate(body.password.validate()) { "Password isn't strong enough" }
    validate(body.name.isNotBlank()) { "Name cannot be empty" }

    val hashedPassword = ad.config.argon2.hash(body.password)
    val user = UserDbo(
        id = newId(),
        username = body.username,
        email = body.email,
        password = hashedPassword,
        name = body.name,
    )

    val inserted = insertIgnoreDuplicate {
        UserDbo.collection(ad.connection).insertOne(user)
    }
    if (inserted) {
        call.respond(HttpStatusCode.Created)
    } else {
        call.respond(HttpStatusCode.Conflict.description("User with same username or email already exist"))
    }
}
