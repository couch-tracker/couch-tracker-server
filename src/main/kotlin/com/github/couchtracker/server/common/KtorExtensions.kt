package com.github.couchtracker.server.common

import com.github.couchtracker.server.IgnoreException
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.util.pipeline.*
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

@OptIn(ExperimentalContracts::class)
suspend fun PipelineContext<Unit, ApplicationCall>.validate(
    condition: Boolean,
    f: suspend ApplicationCall.() -> Unit = {
        respond(HttpStatusCode.BadRequest)
    },
) {
    contract {
        returns() implies condition
    }
    if (!condition) {
        call.f()
        throw IgnoreException()
    }
}