package com.github.couchtracker.server.model.api

import com.github.couchtracker.server.model.api.base.PatchObject
import com.github.couchtracker.server.model.db.UserDbo
import com.github.couchtracker.server.util.Email
import com.github.couchtracker.server.util.OptionalField
import com.github.couchtracker.server.util.OptionalField.Missing
import com.github.couchtracker.server.util.Password
import com.github.couchtracker.server.util.Username
import com.github.couchtracker.server.util.setOptionals
import com.github.couchtracker.server.util.setTo
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String,
    val username: Username,
    val email: Email,
    val name: String,
)

@Serializable
data class UserPatch(
    val email: OptionalField<Email> = Missing,
    val name: OptionalField<String> = Missing,
) : PatchObject<UserDbo> {

    override val dbo get() = UserDbo

    override fun updateBson() = setOptionals(
        UserDbo::email setTo email,
        UserDbo::name setTo name,
    )
}

@Serializable
data class UserDelete(
    val password: Password,
)
