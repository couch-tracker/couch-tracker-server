package com.github.couchtracker.server.util

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe

class UsernameTest : FunSpec(
    {

        context("valid usernames") {
            withData(
                nameFn = { "Username: '$it'" },
                "test123",
                "123test",
                "TEST",
                "test",
                "HELLO-WORLD",
                "HELLO_WORLD",
            ) { username ->
                Username(username).value shouldBe username
            }
        }

        context("invalid usernames") {
            withData(
                nameFn = { "Username: '$it'" },
                "",
                "a",
                "ab",
                "abc",
                "ABC",
                "USERNAME-THAT-IS-DEFINITELY-TOO-LONG",
                "-CANNOT-START-WITH-SPECIAL-CHAR",
                "CANNOT-END-WITH-SPECIAL-CHAR_",
                "hello!world",
                "hello#world",
                "hello@world",
                "no-accènts",
                "nò-accents",
            ) { username ->
                shouldThrow<IllegalArgumentException> {
                    Username(username)
                }
            }
        }
    },
)
