package com.github.couchtracker.server.model.externalIds

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class ExternalIdTests {

    @Test
    fun testDeserialize() {
        val actual = Json.decodeFromString<ExternalId>("\"tmdb:1234\"")
        assertEquals(TmdbExternalId(1234), actual)

        assertThrows<SerializationException> {
            Json.decodeFromString<ExternalId>("\"tmdb:invalid\"")
        }

        assertThrows<SerializationException> {
            Json.decodeFromString<ExternalId>("\"invalid:1234\"")
        }
    }

    @Test
    fun testDeserializeSubtype() {
        val actual = Json.decodeFromString<TmdbExternalId>("\"tmdb:1234\"")
        assertEquals(TmdbExternalId(1234), actual)

        assertThrows<SerializationException> {
            Json.decodeFromString<TmdbExternalId>("\"tmdb:invalid\"")
        }

        assertThrows<SerializationException> {
            Json.decodeFromString<TmdbExternalId>("\"tvdb:1234\"")
        }
    }

    @Test
    fun testSerialize() {
        val actual = Json.encodeToString(TmdbExternalId(1234))
        assertEquals("\"tmdb:1234\"", actual)
    }
}
