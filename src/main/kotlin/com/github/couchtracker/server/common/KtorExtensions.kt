package com.github.couchtracker.server.common

import com.github.couchtracker.server.ApplicationData
import com.github.couchtracker.server.IgnoreException
import com.github.couchtracker.server.common.model.ExternalIdProvider
import com.github.couchtracker.server.infoProviders.InfoProvider
import com.github.couchtracker.server.infoProviders.TvApis
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

suspend fun PipelineContext<Unit, ApplicationCall>.getInfoProvider(
    applicationData: ApplicationData,
    provider: ExternalIdProvider,
): InfoProvider {
    val infoProvider = applicationData.infoProviders[provider]
    validate(infoProvider != null) {
        respond(HttpStatusCode.NotImplemented, "This server doesn't support IDs from provider: $provider")
    }
    return infoProvider
}

suspend fun PipelineContext<Unit, ApplicationCall>.tvApis(
    applicationData: ApplicationData,
    provider: ExternalIdProvider,

): TvApis<String> {
    val infoProvider = getInfoProvider(applicationData, provider)
    return apis(infoProvider, "shows", infoProvider.tvApis)
}

private suspend fun <T : Any> PipelineContext<Unit, ApplicationCall>.apis(
    infoProvider: InfoProvider,
    typeName: String,
    apis: T?
): T {
    validate(apis != null) {
        respond(HttpStatusCode.BadRequest, "${infoProvider.externalIdProvider} doesn't support $typeName.")
    }
    return apis
}