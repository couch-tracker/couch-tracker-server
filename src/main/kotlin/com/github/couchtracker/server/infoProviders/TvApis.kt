package com.github.couchtracker.server.infoProviders

import com.github.couchtracker.server.common.model.Video
import com.github.couchtracker.server.common.model.shows.Show


interface TvApis<ID : Any> {
    fun show(id: ID): ShowApis

    fun <T : Any> convert(convert: (T) -> ID): TvApis<T> {
        val original = this
        return object : TvApis<T> {
            override fun show(id: T): ShowApis {
                return original.show(convert(id))
            }
        }
    }
}

interface ShowApis {
    val info: ApiItem<Show>
    val videos: ApiItem<List<Video>>
}


