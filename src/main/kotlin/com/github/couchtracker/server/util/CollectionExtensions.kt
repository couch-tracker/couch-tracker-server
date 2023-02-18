package com.github.couchtracker.server.util

fun <K, V, R : V?> MutableMap<K, V>.update(key: K, f: (K, V?) -> R): R {
    @Suppress("UNCHECKED_CAST")
    return compute(key, f) as R
}

fun <K, V> Map<out K?, V>.filterNotNullKeys(): Map<K, V> {
    @Suppress("UNCHECKED_CAST")
    return filter { it.key != null } as Map<K, V>
}
