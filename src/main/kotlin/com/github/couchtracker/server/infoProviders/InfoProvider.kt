package com.github.couchtracker.server.infoProviders

import com.github.couchtracker.server.model.common.externalIds.ExternalId

interface InfoProvider {

    val tvApis: TvApis<ExternalId>?
}

class InfoProviders(val providers: Set<InfoProvider>) {

    inline fun <reified IP : InfoProvider> get() = providers.filterIsInstance<IP>().singleOrNull()
}
