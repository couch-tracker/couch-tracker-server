package com.github.couchtracker.server.util.serializers

import io.kotest.assertions.json.shouldEqualJson
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import java.util.Locale
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

class LocaleSerializerTest : FunSpec(
    {

        context("serialization") {
            test("with no country") {
                Json.encodeToString(LocaleSerializer, Locale("it", "")) shouldEqualJson """ "it" """
            }
            test("with country") {
                Json.encodeToString(LocaleSerializer, Locale("it", "IT")) shouldEqualJson """ "it-IT" """
            }
            test("empty locale is not supported") {
                shouldThrow<SerializationException> {
                    Json.encodeToString(LocaleSerializer, Locale(""))
                }
            }
            test("variant is not supported") {
                shouldThrow<SerializationException> {
                    Json.encodeToString(LocaleSerializer, Locale("it", "IT", "someVariant"))
                }
            }
            test("script is not supported") {
                shouldThrow<SerializationException> {
                    Json.encodeToString(LocaleSerializer, Locale.Builder().setLanguage("it").setScript("Latn").build())
                }
            }
            test("extension is not supported") {
                shouldThrow<SerializationException> {
                    Json.encodeToString(LocaleSerializer, Locale.Builder().setLanguage("it").setExtension('x', "abc").build())
                }
            }
        }

        context("deserialization") {
            context("works") {
                test("with no country") {
                    Json.decodeFromString(LocaleSerializer, """ "it" """) shouldBe Locale("it")
                }
                test("with country") {
                    Json.decodeFromString(LocaleSerializer, """ "it-IT" """) shouldBe Locale("it", "IT")
                }
            }

            context("doesn't work") {
                test("with short language") {
                    shouldThrow<SerializationException> {
                        Json.decodeFromString(LocaleSerializer, """ "x-IT" """)
                    }
                }
                test("with long language") {
                    shouldThrow<SerializationException> {
                        Json.decodeFromString(LocaleSerializer, """ "someverylonglanguage-IT" """)
                    }
                }
                test("with short country") {
                    shouldThrow<SerializationException> {
                        Json.decodeFromString(LocaleSerializer, """ "it-X" """)
                    }
                }
                test("with long country") {
                    shouldThrow<SerializationException> {
                        Json.decodeFromString(LocaleSerializer, """ "it-VERYLONGCOUNTRY" """)
                    }
                }
                test("with trailing dash") {
                    shouldThrow<SerializationException> {
                        Json.decodeFromString(LocaleSerializer, """ "it-" """)
                    }
                }
                test("with empty string") {
                    shouldThrow<SerializationException> {
                        Json.decodeFromString(LocaleSerializer, """ "" """)
                    }
                }
                test("with leading spaces") {
                    shouldThrow<SerializationException> {
                        Json.decodeFromString(LocaleSerializer, """ " it-IT" """)
                    }
                }
                test("with trailing spaces") {
                    shouldThrow<SerializationException> {
                        Json.decodeFromString(LocaleSerializer, """ "it-IT " """)
                    }
                }
                context("with invalid separator") {
                    // Includes non-standard dashes: figure dash, en dash, em dash
                    withData(nameFn = { "Separator: '$it'" }, "_", ".", " ", "/", "", "‒", "–", "—") { separator ->
                        shouldThrow<SerializationException> {
                            Json.decodeFromString(LocaleSerializer, """ "it${separator}IT" """)
                        }
                    }
                }
                context("with different casing") {
                    withData(
                        "it-it",
                        "IT-it",
                        "IT-IT",
                        "IT",
                        "iT",
                    ) { str ->
                        shouldThrow<SerializationException> {
                            Json.decodeFromString(LocaleSerializer, """ "$str" """)
                        }
                    }
                }
            }
        }
    },
)
