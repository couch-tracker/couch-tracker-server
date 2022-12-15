package com.github.couchtracker.server.infoProviders

import com.github.couchtracker.server.model.Image
import com.github.couchtracker.server.model.Video
import com.github.couchtracker.server.model.shows.Show
import com.github.couchtracker.server.model.shows.ShowImages


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
    val info: ApiItem<Show>
    val images: ApiItem<ShowImages<Image>>
    val videos: ApiItem<List<Video>>
}


