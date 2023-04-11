package com.github.couchtracker.server.util

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotest.matchers.types.shouldBeSameInstanceAs
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.verify
import org.junit.jupiter.api.assertThrows

class ValidationTest : FunSpec(
    {

        val noMessage = { error("Lazy message shouldn't have been called") }

        context("validate()") {
            test("success") {
                validate(true, noMessage) shouldBeSameInstanceAs ValidationResult.Success
            }
            test("error") {
                val validation = validate(false) { "some error" }
                validation shouldBe ValidationResult.Error.Single("some error")
            }
        }

        context("runValidations()") {
            test("empty") {
                runValidations { } shouldBeSameInstanceAs ValidationResult.Success
            }
            test("successes") {
                val result = runValidations {
                    validate(true, noMessage) shouldBeSameInstanceAs ValidationResult.Success
                    validate(true, noMessage) shouldBeSameInstanceAs ValidationResult.Success
                }
                result shouldBeSameInstanceAs ValidationResult.Success
            }
            test("single error") {
                lateinit var validation: ValidationResult
                val result = runValidations {
                    validation = validate(false) { "test error" }
                }
                result shouldBe ValidationResult.Error.Single("test error")
                validation shouldBeSameInstanceAs result
            }
            test("multiple errors") {
                lateinit var err1: ValidationResult.Error
                lateinit var err2: ValidationResult.Error
                val result = runValidations {
                    validate(true, noMessage)
                    err1 = validate(false) { "test error 1" }.shouldBeInstanceOf<ValidationResult.Error.Single>()
                    err1.message shouldBe "test error 1"
                    err2 = validate(false) { "test error 2" }.shouldBeInstanceOf<ValidationResult.Error.Single>()
                    err2.message shouldBe "test error 2"
                }

                result.shouldBeInstanceOf<ValidationResult.Error.Multiple>()
                result.errors.size shouldBe 2
                result.errors[0] shouldBeSameInstanceAs err1
                result.errors[1] shouldBeSameInstanceAs err2
            }
            test("error hierarchy") {
                val complex = runValidations {
                    validate(false) { "error 1" }
                    runValidations {
                        validate(false) { "inner error 1" }
                        validate(true, noMessage)
                        validate(false) { "inner error 2" }
                    }
                    validate(false) { "error 2" }
                }
                complex shouldBe ValidationResult.Error.Multiple(
                    listOf(
                        ValidationResult.Error.Single("error 1"),
                        ValidationResult.Error.Multiple(
                            listOf(
                                ValidationResult.Error.Single("inner error 1"),
                                ValidationResult.Error.Single("inner error 2"),
                            ),
                        ),
                        ValidationResult.Error.Single("error 2"),
                    ),
                )
            }
        }

        context("coalesce()") {
            test("empty list") {
                emptyList<ValidationResult>().coalesce() shouldBeSameInstanceAs ValidationResult.Success
            }
            test("successes") {
                listOf(
                    validate(true, noMessage),
                    validate(true, noMessage),
                ).coalesce() shouldBeSameInstanceAs ValidationResult.Success
            }
            test("mixed") {
                val err1 = ValidationResult.Error.Single("error 1")
                val err2 = ValidationResult.Error.Single("error 2")
                val result = listOf(ValidationResult.Success, err1, err2).coalesce()
                result.shouldBeInstanceOf<ValidationResult.Error.Multiple>()
                result.errors.size shouldBe 2
                result.errors[0] shouldBeSameInstanceAs err1
                result.errors[1] shouldBeSameInstanceAs err2
            }
        }

        context("flatten()") {
            test("empty") {
                listOf<ValidationResult.Error>().flatten() shouldBe emptyList()
            }
            test("already flattened") {
                val err1 = ValidationResult.Error.Single("error 1")
                val err2 = ValidationResult.Error.Single("error 2")
                val result = listOf(err1, err2).flatten()
                result.size shouldBe 2
                result[0] shouldBeSameInstanceAs err1
                result[1] shouldBeSameInstanceAs err2
            }
            test("1 depth") {
                val err1 = ValidationResult.Error.Single("error 1")
                val innerErr1 = ValidationResult.Error.Single("inner error 1")
                val innerErr2 = ValidationResult.Error.Single("inner error 2")
                val err2 = ValidationResult.Error.Multiple(listOf(innerErr1, innerErr2))
                val result = listOf(err1, err2).flatten()

                result.size shouldBe 3
                result[0] shouldBeSameInstanceAs err1
                result[1] shouldBeSameInstanceAs innerErr1
                result[2] shouldBeSameInstanceAs innerErr2
            }
            test("2 depth") {
                val err1 = ValidationResult.Error.Single("error 1")
                val innerErr1 = ValidationResult.Error.Single("inner error 1")
                val innerInnerErr1 = ValidationResult.Error.Single("inner inner error 1")
                val innerInnerErr2 = ValidationResult.Error.Single("inner inner error 2")
                val innerErr2 = ValidationResult.Error.Multiple(listOf(innerInnerErr1, innerInnerErr2))
                val err2 = ValidationResult.Error.Multiple(listOf(innerErr1, innerErr2))
                val result = listOf(err1, err2).flatten()

                result.size shouldBe 4
                result[0] shouldBeSameInstanceAs err1
                result[1] shouldBeSameInstanceAs innerErr1
                result[2] shouldBeSameInstanceAs innerInnerErr1
                result[3] shouldBeSameInstanceAs innerInnerErr2
            }
        }

        test("flattened()") {
            mockkStatic(Collection<ValidationResult.Error>::flatten) {
                val list = listOf(
                    ValidationResult.Error.Single("error 1"),
                    ValidationResult.Error.Single("error 2"),
                )
                every { list.flatten() } answers { callOriginal() }
                ValidationResult.Error.Multiple(list).flattened()
                verify(exactly = 1) { list.flatten() }
            }
        }

        context("require()") {
            test("success") {
                ValidationResult.Success.require()
            }
            test("single") {
                val err = ValidationResult.Error.Single("error 1")
                assertThrows<IllegalArgumentException>(noMessage) {
                    err.require()
                }.also {
                    it.message shouldBe err.message
                }
            }
            test("multiple") {
                val err = ValidationResult.Error.Multiple(
                    listOf(
                        ValidationResult.Error.Single("error 1"),
                        ValidationResult.Error.Single("error 2"),
                    ),
                )
                assertThrows<IllegalArgumentException>(noMessage) {
                    err.require()
                }.also {
                    it.message shouldBe err.message
                }
            }
        }

        context("check()") {
            test("success") {
                ValidationResult.Success.check()
            }
            test("single") {
                val err = ValidationResult.Error.Single("error 1")
                assertThrows<IllegalStateException>(message = err.message) {
                    err.check()
                }
            }
            test("multiple") {
                val err = ValidationResult.Error.Multiple(
                    listOf(
                        ValidationResult.Error.Single("error 1"),
                        ValidationResult.Error.Single("error 2"),
                    ),
                )
                assertThrows<IllegalStateException>(message = err.message) {
                    err.check()
                }
            }
        }

        context("multiple") {
            test("cannot be constructed with no errors") {
                assertThrows<IllegalArgumentException> { ValidationResult.Error.Multiple(emptyList()) }
            }
            test("cannot be constructed with just one error") {
                assertThrows<IllegalArgumentException> { ValidationResult.Error.Multiple(listOf(ValidationResult.Error.Single("err"))) }
            }
            context("message") {
                test("simple") {
                    val err = ValidationResult.Error.Multiple(
                        listOf(
                            ValidationResult.Error.Single("err 1"),
                            ValidationResult.Error.Single("err 2"),
                            ValidationResult.Error.Single("err 3"),
                        ),
                    )
                    err.message shouldBe buildString {
                        appendLine("3 validation errors:")
                        appendLine("\t1. err 1")
                        appendLine("\t2. err 2")
                        append("\t3. err 3")
                    }
                }
                test("with hierarchy") {
                    val err = ValidationResult.Error.Multiple(
                        listOf(
                            ValidationResult.Error.Single("err 1"),
                            ValidationResult.Error.Multiple(
                                listOf(
                                    ValidationResult.Error.Single("inner err 1"),
                                    ValidationResult.Error.Single("inner err 2"),
                                ),
                            ),
                            ValidationResult.Error.Single("err 3"),
                        ),
                    )
                    err.message shouldBe buildString {
                        appendLine("3 validation errors:")
                        appendLine("\t1. err 1")
                        appendLine("\t2. 2 validation errors:")
                        appendLine("\t\t1. inner err 1")
                        appendLine("\t\t2. inner err 2")
                        append("\t3. err 3")
                    }
                }
            }
        }
    },
)
