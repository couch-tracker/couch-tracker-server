package com.github.couchtracker.server.common.serializers

import kotlin.reflect.KClass

abstract class EnumIdSerializer<T : Enum<T>>(
    private val enumClass: KClass<out T>,
    private val getId: T.() -> String,
    name: String = enumClass.simpleName ?: throw IllegalArgumentException("enumClass must have a name"),
) : StringSerializer<T>(
    name = name,
    serialize = { it.getId() },
    deserialize = { str -> enumClass.java.enumConstants.single { it.getId() == str } }
)