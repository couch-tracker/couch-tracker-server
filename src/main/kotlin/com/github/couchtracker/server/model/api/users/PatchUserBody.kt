package com.github.couchtracker.server.model.api.users

import com.github.couchtracker.server.model.api.base.PatchObject
import com.github.couchtracker.server.model.db.UserDbo
import com.github.couchtracker.server.util.Email
import com.github.couchtracker.server.util.OptionalField
import com.github.couchtracker.server.util.setOptionals
import com.github.couchtracker.server.util.setTo
import kotlinx.serialization.Serializable

@Serializable
data class PatchUserBody(
    val email: OptionalField<Email> = OptionalField.Missing,
    val name: OptionalField<String> = OptionalField.Missing,
) : PatchObject<UserDbo> {

    override val dbo get() = UserDbo

    override fun updateBson() = setOptionals(
        UserDbo::email setTo email,
        UserDbo::name setTo name,
    )
}
