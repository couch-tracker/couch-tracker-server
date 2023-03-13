package com.github.couchtracker.server.util.serializers

import org.litote.kmongo.id.WrappedObjectId

class WrappedObjectIdSerializer<T> : StringSerializer<WrappedObjectId<T>>(
    name = "WrappedObjectId",
    serialize = { it.toString() },
    deserialize = { WrappedObjectId(it) },
)
