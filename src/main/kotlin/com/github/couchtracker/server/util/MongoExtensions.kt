package com.github.couchtracker.server.util

import com.mongodb.MongoWriteException

private const val DUPLICATE_KEY_ERROR_CODE = 11000

fun MongoWriteException.isDuplicateKey() = error.code == DUPLICATE_KEY_ERROR_CODE

suspend fun insertIgnoreDuplicate(insert: suspend () -> Unit): Boolean {
    return try {
        insert()
        true
    } catch (e: MongoWriteException) {
        when {
            e.isDuplicateKey() -> false
            else -> throw e
        }
    }
}
