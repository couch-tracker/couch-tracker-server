@file:UseSerializers(WrappedObjectIdSerializer::class)

package com.github.couchtracker.server.routes.users

import com.github.couchtracker.server.ApplicationData
import com.github.couchtracker.server.accessPrincipal
import com.github.couchtracker.server.model.api.users.DeleteUserBody
import com.github.couchtracker.server.model.api.users.PatchUserBody
import com.github.couchtracker.server.model.db.UserDbo
import com.github.couchtracker.server.util.serializers.WrappedObjectIdSerializer
import com.github.couchtracker.server.util.validate
import io.ktor.http.HttpStatusCode
import io.ktor.resources.Resource
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.resources.delete
import io.ktor.server.resources.get
import io.ktor.server.resources.patch
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.util.pipeline.PipelineContext
import org.litote.kmongo.Id
import org.litote.kmongo.id.WrappedObjectId
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
@Resource("/users")
class Users {

    @Serializable
    @Resource("{userId}")
    data class Id(val parent: Users, val userId: WrappedObjectId<UserDbo>)
}

fun Route.users(ad: ApplicationData) {
    get<Users.Id> { (_, id) ->
        val user = checkSelf(id)
        call.respond(user.toApi())
    }

    patch<Users.Id> { (_, id) ->
        val user = checkSelf(id)
        val patch = call.receive<PatchUserBody>()
        patch.update(ad.db, user.id)
        call.respond(HttpStatusCode.NoContent)
    }

    delete<Users.Id> { (_, id) ->
        val user = checkSelf(id)
        val (password) = call.receive<DeleteUserBody>()
        if (!ad.config.argon2.verify(user.password, password)) {
            call.respond(HttpStatusCode.Unauthorized.description("Password is incorrect"))
        } else {
            UserDbo.collection(ad.db).deleteOneById(user.id)
            call.respond(HttpStatusCode.NoContent)
        }
    }
    lists(ad)
    showCollection(ad)
}

suspend fun PipelineContext<Unit, ApplicationCall>.checkSelf(id: Id<UserDbo>): UserDbo {
    val user = call.accessPrincipal.user
    validate(user.id.toString() == id.toString(), HttpStatusCode.Forbidden)
    return user
}
