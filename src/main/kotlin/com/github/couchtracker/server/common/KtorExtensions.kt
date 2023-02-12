package com.github.couchtracker.server.common

import com.github.couchtracker.server.ApplicationData
import com.github.couchtracker.server.IgnoreException
import com.github.couchtracker.server.infoProviders.TvApis
import com.github.couchtracker.server.model.externalIds.ExternalId
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.application.log
import io.ktor.server.response.respond
import io.ktor.util.pipeline.PipelineContext
import mu.KotlinLogging
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

@OptIn(ExperimentalContracts::class)
suspend fun PipelineContext<Unit, ApplicationCall>.validate(
    condition: Boolean,
    code: HttpStatusCode = HttpStatusCode.BadRequest,
    message: suspend () -> String? = { null },
) {
    contract {
        returns() implies condition
    }
    if (!condition) {
        val msg = message()
        call.respond(
            when {
                msg != null -> code.description(msg)
                else -> code
            }
        )
        throw IgnoreException()
    }
}

val PipelineContext<Unit, ApplicationCall>.log
    get() = KotlinLogging.logger(call.application.log)

suspend fun PipelineContext<Unit, ApplicationCall>.tvApis(
    applicationData: ApplicationData,
    id: ExternalId,
): TvApis<ExternalId> {
    val infoProvider = id.getInfoProvider(applicationData.infoProviders)
    validate(infoProvider != null, HttpStatusCode.NotImplemented) {
        "This server doesn't support IDs from provider ${id.type}!"
    }
    return apis(id, "shows", infoProvider.tvApis)
}

private suspend fun <T : Any> PipelineContext<Unit, ApplicationCall>.apis(
    id: ExternalId,
    typeName: String,
    apis: T?,
): T {
    validate(apis != null, HttpStatusCode.BadRequest) {
        "${id.type} doesn't support $typeName."
    }
    return apis
}
