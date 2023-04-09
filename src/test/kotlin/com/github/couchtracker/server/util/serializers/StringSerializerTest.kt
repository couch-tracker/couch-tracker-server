package com.github.couchtracker.server.util.serializers

import io.kotest.assertions.json.shouldEqualJson
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class StringSerializerTest : FunSpec(
    {
        context("serialization") {
            test("works") {
                val serializer = object : StringSerializer<Unit>(
                    name = "Test",
                    serialize = { "test" },
                    deserialize = { TODO() },
                ) {}
                Json.encodeToString(serializer, Unit) shouldEqualJson """ "test" """
            }

            test("illegal arguments are wrapped in SerializationException") {
                val exception = IllegalArgumentException()
                val serializer = object : StringSerializer<Unit>(
                    name = "Test",
                    serialize = { throw exception },
                    deserialize = { TODO() },
                ) {}
                val actualException = shouldThrowExactly<SerializationException> {
                    Json.encodeToString(serializer, Unit)
                }
                actualException.cause shouldBeSameInstanceAs exception
            }

            test("other exceptions are passed through") {
                val exception = IllegalStateException()
                val serializer = object : StringSerializer<Unit>(
                    name = "Test",
                    serialize = { throw exception },
                    deserialize = { TODO() },
                ) {}
                val actualException = shouldThrowExactly<IllegalStateException> {
                    Json.encodeToString(serializer, Unit)
                }
                actualException shouldBeSameInstanceAs exception
            }
        }

        context("deserialization") {
            test("works") {
                val serializer = object : StringSerializer<Int>(
                    name = "Test",
                    serialize = { TODO() },
                    deserialize = { it.toInt() },
                ) {}
                Json.decodeFromString(serializer, """ "123" """) shouldBe 123
            }

            test("illegal arguments are wrapped in SerializationException") {
                val exception = IllegalArgumentException()
                val serializer = object : StringSerializer<Int>(
                    name = "Test",
                    serialize = { TODO() },
                    deserialize = { throw exception },
                ) {}
                val actualException = shouldThrowExactly<SerializationException> {
                    Json.decodeFromString(serializer, """ "" """)
                }
                actualException.cause shouldBeSameInstanceAs exception
            }

            test("other exceptions are passed through") {
                val exception = IllegalStateException()
                val serializer = object : StringSerializer<Int>(
                    name = "Test",
                    serialize = { TODO() },
                    deserialize = { throw exception },
                ) {}
                val actualException = shouldThrowExactly<IllegalStateException> {
                    Json.decodeFromString(serializer, """ "" """)
                }
                actualException shouldBeSameInstanceAs exception
            }
        }
    },
)
