package com.github.couchtracker.server.util

import com.mongodb.MongoWriteException
import org.bson.conversions.Bson
import org.litote.kmongo.SetTo
import org.litote.kmongo.set
import org.litote.kmongo.setTo
import kotlin.reflect.KProperty

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

@Suppress("SpreadOperator")
fun setOptionals(vararg properties: SetTo<*>?): Bson? {
    val filtered = properties.filterNotNull()
    return when {
        filtered.isNotEmpty() -> set(*filtered.toTypedArray())
        else -> null
    }
}

infix fun <T> KProperty<T>.setTo(optional: OptionalField<T>): SetTo<T>? {
    return when (optional) {
        is OptionalField.Missing -> null
        is OptionalField.Present -> this setTo optional.value
    }
}
