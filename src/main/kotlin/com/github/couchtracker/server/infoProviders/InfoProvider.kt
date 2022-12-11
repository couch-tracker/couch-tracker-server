package com.github.couchtracker.server.infoProviders

import com.github.couchtracker.server.common.model.ExternalIdProvider

interface InfoProvider {

    val externalIdProvider: ExternalIdProvider

    val tvApis: TvApis<String>?
}

class InfoProviders(providers: Set<InfoProvider>) {
    private val map = providers.associateBy { it.externalIdProvider }

    operator fun get(provider: ExternalIdProvider) = map[provider]
}
