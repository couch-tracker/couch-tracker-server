package com.github.couchtracker.server.util

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe

class EmailTest : FunSpec(
    {

        context("valid emails") {
            withData(
                nameFn = { "Email: '$it'" },
                "test@test.com",
                "a@a.a",
                "x@x",
            ) { email ->
                Email(email).value shouldBe email
            }
        }

        context("invalid emails") {
            withData(
                nameFn = { "Email: '$it'" },
                "NOT A VALID EMAIL",
                "a@",
                "@a",
                "",
            ) { email ->
                shouldThrow<IllegalArgumentException> {
                    Email(email)
                }
            }
        }
    },
)
