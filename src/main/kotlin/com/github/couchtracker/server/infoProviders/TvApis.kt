package com.github.couchtracker.server.infoProviders

import com.github.couchtracker.server.model.common.ShowImages
import com.github.couchtracker.server.model.common.Video
import com.github.couchtracker.server.model.infoProviders.ShowInfo

interface TvApis<ID : Any> {

    fun show(id: ID): ShowApis

    fun <T : Any> convert(convert: (T) -> ID): TvApis<T> {
        val original = this
        return object : TvApis<T> {
            override fun show(id: T) = original.show(convert(id))
        }
    }
}

interface ShowApis {
    val info: ApiItem<ShowInfo>
    val images: ApiItem<ShowImages>
    val videos: ApiItem<List<Video>>
}
