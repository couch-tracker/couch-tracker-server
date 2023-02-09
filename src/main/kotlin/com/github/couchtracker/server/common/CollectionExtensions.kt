package com.github.couchtracker.server.common

fun <K, V, R : V?> MutableMap<K, V>.update(key: K, f: (K, V?) -> R): R {
    @Suppress("UNCHECKED_CAST")
    return compute(key, f) as R
}
