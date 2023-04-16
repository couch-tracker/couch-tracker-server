package com.github.couchtracker.server.util

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

class PasswordTest : FunSpec(
    {

        context("valid passwords") {
            withData(
                nameFn = { "Password: '$it'" },
                "aLotOfSpecialCharsPassword123456789!$&%()=?!\"'@#ù§è+é**ì^?\\|<>,.-;:_ò",
                "loooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooongPassword1234!",
                "lowerUPPER123!",
                "lowerUPPER123",
                "lowerUPPER!",
                "lower123!",
                "UPPER123!",
            ) { password ->
                Password(password).also {
                    it.value shouldBe password
                    it.validate() shouldBe ValidationResult.Success
                }
            }
        }

        context("invalid passwords") {
            withData(
                nameFn = { "Password: '$it'" },
                "",
                "short!1",
                "onlylowercase",
                "ONLYUPPERCASE",
                "1234567890",
                "onlylowerandUPPERcase",
                "onlylower123",
                "ONLYUPPER123",
                "\nwithNewline",
                "\twithTab",
            ) { password ->
                Password(password).also {
                    it.value shouldBe password
                    it.validate().shouldBeInstanceOf<ValidationResult.Error>()
                }
            }
        }
    },
)
