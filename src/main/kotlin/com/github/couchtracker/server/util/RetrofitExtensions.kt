package com.github.couchtracker.server.util

import retrofit2.Call

fun <T : Any> Call<T?>.makeNotNull(): Call<T> {
    @Suppress("UNCHECKED_CAST")
    return this as Call<T>
}
