package com.github.couchtracker.server

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.Payload
import com.github.couchtracker.server.db.model.UserDbo
import com.github.couchtracker.server.model.LoginTokens
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import kotlinx.datetime.Clock
import kotlinx.datetime.toKotlinInstant
import org.bson.types.ObjectId
import java.util.*
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours


object JWT {

    object Login {
        const val ACCESS = "login-access"
        const val REFRESH = "login-refresh"
        private const val CLAIM_USER = "user"
        private val ACCESS_EXPIRATION = 8.hours
        private val REFRESH_EXPIRATION = 28.days

        class AccessPrincipal(val user: UserDbo, payload: Payload) : Principal, JWTPayloadHolder(payload)
        class RefreshPrincipal(val user: UserDbo, payload: Payload) : Principal, JWTPayloadHolder(payload)

        fun install(authConfig: AuthenticationConfig, data: ApplicationData) {
            fun JWTAuthenticationProvider.Config.verify(audience: String) {
                verifier(
                    JWT.require(Algorithm.HMAC256(data.config.jwt.secret.value))
                        .withAudience(audience)
                        .build()
                )
            }

            suspend fun JWTCredential.validate(createPrincipal: (UserDbo, Payload) -> Principal?) : Principal? {
                val userId = payload.getClaim(CLAIM_USER).asString()
                val user = UserDbo.collection(data.connection).findOneById(ObjectId(userId)) ?: return null

                val issuedAtInstant = issuedAt?.toInstant()?.toKotlinInstant() ?: return null
                return if(user.invalidateTokensAfter != null && issuedAtInstant < user.invalidateTokensAfter) null
                else createPrincipal(user, payload)
            }

            authConfig.jwt(ACCESS) {
                verify(ACCESS)
                validate { it.validate(::AccessPrincipal) }
                challenge { _, _ ->
                    call.respond(HttpStatusCode.Unauthorized.description("Token is not valid or has expired"))
                }
            }

            authConfig.jwt(REFRESH) {
                verify(REFRESH)
                validate { it.validate(::RefreshPrincipal) }
                challenge { _, _ ->
                    call.respond(HttpStatusCode.Unauthorized.description("Token is not valid or has expired"))
                }
            }
        }

        // TODO: add mechanism to invalidate refresh_token when a new access token is granted
        fun generate(ad: ApplicationData, user: UserDbo): LoginTokens {
            return LoginTokens(
                token = JWT.create()
                    .withAudience(ACCESS)
                    .withClaim(CLAIM_USER, user.id.toString())
                    .withIssuedAt(Date())
                    .withExpiresAt(Date((Clock.System.now() + ACCESS_EXPIRATION).toEpochMilliseconds()))
                    .sign(Algorithm.HMAC256(ad.config.jwt.secret.value)),
                refreshToken = JWT.create()
                    .withAudience(REFRESH)
                    .withClaim(CLAIM_USER, user.id.toString())
                    .withIssuedAt(Date())
                    .withExpiresAt(Date((Clock.System.now() + REFRESH_EXPIRATION).toEpochMilliseconds()))
                    .sign(Algorithm.HMAC256(ad.config.jwt.secret.value)),
            )
        }
    }
}
