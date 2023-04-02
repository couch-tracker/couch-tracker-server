package com.github.couchtracker.server.model.externalIds

import com.github.couchtracker.server.model.common.externalIds.ExternalId
import com.github.couchtracker.server.model.common.externalIds.TmdbExternalId
import io.kotest.assertions.json.shouldEqualJson
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class ExternalIdTests : FunSpec(
    {

        context("deserialization") {
            context("with main type") {
                test("is successful") {
                    val actual = Json.decodeFromString<ExternalId>("\"tmdb:1234\"")
                    actual shouldBe TmdbExternalId(1234)
                }
                test("throws on invalid type") {
                    shouldThrow<SerializationException> {
                        Json.decodeFromString<ExternalId>("\"invalid:1234\"")
                    }
                }
                test("throws on invalid id") {
                    shouldThrow<SerializationException> {
                        Json.decodeFromString<ExternalId>("\"tmdb:invalid\"")
                    }
                }
            }

            context("with subtype") {
                test("is successful") {
                    val actual = Json.decodeFromString<TmdbExternalId>("\"tmdb:1234\"")
                    actual shouldBe TmdbExternalId(1234)
                }
                test("throws on invalid type") {
                    shouldThrow<SerializationException> {
                        Json.decodeFromString<TmdbExternalId>("\"tvdb:1234\"")
                    }
                }
                test("throws on invalid id") {
                    shouldThrow<SerializationException> {
                        Json.decodeFromString<TmdbExternalId>("\"tmdb:invalid\"")
                    }
                }
            }
        }

        test("serialize") {
            val actual = Json.encodeToString(TmdbExternalId(1234))
            actual shouldEqualJson """ "tmdb:1234" """
        }
    },
)
