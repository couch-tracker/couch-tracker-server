package com.github.couchtracker.server.util

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.types.shouldBeSameInstanceAs
import io.mockk.mockk
import retrofit2.Call

class RetrofitExtensionsTest : FunSpec(
    {

        test("asNotNull") {
            val call = mockk<Call<String?>>()
            call.asNotNull() shouldBeSameInstanceAs call
        }
    },
)
