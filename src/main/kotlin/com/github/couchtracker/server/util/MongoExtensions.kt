package com.github.couchtracker.server.util

import com.mongodb.ClientSessionOptions
import com.mongodb.MongoWriteException
import com.mongodb.TransactionOptions
import com.mongodb.reactivestreams.client.ClientSession
import org.bson.conversions.Bson
import org.litote.kmongo.SetTo
import org.litote.kmongo.coroutine.CoroutineClient
import org.litote.kmongo.coroutine.commitTransactionAndAwait
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

private suspend fun CoroutineClient.startSessionWithNullableOptions(options: ClientSessionOptions?): ClientSession {
    return when {
        options != null -> startSession(options)
        else -> startSession()
    }
}

private fun ClientSession.startTransactionWithNullableOptions(options: TransactionOptions?) {
    when {
        options != null -> startTransaction(options)
        else -> startTransaction()
    }
}

suspend fun <R> CoroutineClient.transaction(
    sessionOptions: ClientSessionOptions? = null,
    transactionOptions: TransactionOptions? = null,
    block: suspend (session: ClientSession) -> R,
): R {
    return startSessionWithNullableOptions(sessionOptions).use { session ->
        session.startTransactionWithNullableOptions(transactionOptions)
        block(session).also {
            if (session.hasActiveTransaction()) {
                session.commitTransactionAndAwait()
            }
        }
    }
}
