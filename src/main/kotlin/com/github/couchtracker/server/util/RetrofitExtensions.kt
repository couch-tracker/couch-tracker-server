package com.github.couchtracker.server.util

import retrofit2.Call

/**
 * Performs an unchecked cast of the retrofit call to make it non-null
 */
fun <T : Any> Call<T?>.asNotNull(): Call<T> {
    @Suppress("UNCHECKED_CAST")
    return this as Call<T>
}
