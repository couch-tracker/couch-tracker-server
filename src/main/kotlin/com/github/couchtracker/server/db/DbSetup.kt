package com.github.couchtracker.server.db

import com.github.couchtracker.server.db.model.ShowDbo
import com.github.couchtracker.server.common.model.Translation
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.div
import org.litote.kmongo.textIndex

suspend fun CoroutineDatabase.setup() {
    shows().apply {
        ensureIndex((ShowDbo::name / Translation::value).textIndex())
    }
}