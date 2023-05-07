package com.github.couchtracker.server.util

import com.github.couchtracker.server.model.db.UserDbo
import com.mongodb.ClientSessionOptions
import com.mongodb.MongoWriteException
import com.mongodb.TransactionOptions
import com.mongodb.WriteError
import com.mongodb.reactivestreams.client.ClientSession
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.runs
import io.mockk.verify
import org.junit.jupiter.api.assertThrows
import org.litote.kmongo.coroutine.CoroutineClient
import org.litote.kmongo.coroutine.commitTransactionAndAwait
import org.litote.kmongo.set
import org.litote.kmongo.setTo
import com.github.couchtracker.server.util.setTo as setToOF

class MongoExtensionsTest : FunSpec(
    {

        context("insertIgnoreDuplicate()") {
            test("works") {
                val run = mockk<() -> Unit>()
                every { run() } just runs

                insertIgnoreDuplicate(run) shouldBe true
                coVerify(exactly = 1) { run() }
            }

            test("duplicate key exception") {
                val run = mockk<() -> Unit>()
                every { run() } throws MongoWriteException(WriteError(11000, "Duplicate", mockk()), mockk())

                insertIgnoreDuplicate(run) shouldBe false
                coVerify(exactly = 1) { run.invoke() }
            }

            test("another error code") {
                val ex = MongoWriteException(WriteError(1235, "Some other error", mockk()), mockk())
                val run = mockk<() -> Unit>()
                every { run() } throws ex

                assertThrows<MongoWriteException> {
                    insertIgnoreDuplicate(run)
                }.shouldBeSameInstanceAs(ex)
                coVerify(exactly = 1) { run.invoke() }
            }

            test("another exception") {
                val ex = IllegalStateException("Some other exception")
                val run = mockk<() -> Unit>()
                every { run() } throws ex

                assertThrows<IllegalStateException> {
                    insertIgnoreDuplicate(run)
                }.shouldBeSameInstanceAs(ex)
                coVerify(exactly = 1) { run.invoke() }
            }
        }

        context("setOptionals()") {
            test("empty") {
                setOptionals() shouldBe null
            }

            test("with just nulls") {
                setOptionals(null, null) shouldBe null
            }

            test("with one") {
                setOptionals(UserDbo::name setTo "Abcd", null) shouldBe set(UserDbo::name setTo "Abcd")
            }

            test("with multiple") {
                setOptionals(
                    UserDbo::name setTo "Abcd",
                    null,
                    UserDbo::password setTo "password",
                    null,
                ) shouldBe set(UserDbo::name setTo "Abcd", UserDbo::password setTo "password")
            }
        }

        context("setTo() for OptionalField") {
            test("missing") {
                (UserDbo::name setToOF OptionalField.Missing) shouldBe null
            }

            test("present") {
                (UserDbo::name setToOF OptionalField.Present("abcd")) shouldBe (UserDbo::name setTo "abcd")
            }
        }

        context("transaction()") {

            test("works") {
                setupTransactionMocks { (client, session, block) ->
                    coEvery { client.startSession() } returns session
                    coEvery { block(any()) } returns 123

                    client.transaction(block = block)

                    coVerify(exactly = 1) { client.startSession() }
                    verify(exactly = 1) { session.startTransaction() }
                    coVerify(exactly = 1) { block(session) }
                    coVerify(exactly = 1) { session.commitTransactionAndAwait() }
                }
            }

            test("works with options") {
                setupTransactionMocks { (client, session, block) ->
                    val sessionOptions = mockk<ClientSessionOptions>()
                    val transactionOptions = mockk<TransactionOptions>()
                    coEvery { client.startSession(any()) } returns session
                    coEvery { block(any()) } returns 123

                    client.transaction(sessionOptions, transactionOptions, block)

                    coVerify(exactly = 1) { client.startSession(sessionOptions) }
                    verify(exactly = 1) { session.startTransaction(transactionOptions) }
                    coVerify(exactly = 1) { block(session) }
                    coVerify(exactly = 1) { session.commitTransactionAndAwait() }
                }
            }

            test("works with failure") {
                setupTransactionMocks { (client, session, block) ->
                    coEvery { client.startSession() } returns session

                    val exception = RuntimeException("exception text abc")
                    coEvery { block(any()) } throws exception

                    shouldThrow<RuntimeException> { client.transaction(block = block) } shouldBe exception

                    coVerify(exactly = 1) { client.startSession() }
                    verify(exactly = 1) { session.startTransaction() }
                    coVerify(exactly = 1) { block(session) }
                    coVerify(exactly = 0) { session.commitTransactionAndAwait() }
                }
            }
        }
    },
)

private data class TransactionMocks(
    val client: CoroutineClient,
    val session: ClientSession,
    val block: suspend (ClientSession) -> Int,
)

private suspend fun setupTransactionMocks(runTest: suspend (mocks: TransactionMocks) -> Unit) {
    val client = mockk<CoroutineClient>()
    val session = mockk<ClientSession>(relaxed = true)
    every { session.hasActiveTransaction() } returns true
    val block = mockk<suspend (ClientSession) -> Int>()

    mockkStatic(ClientSession::commitTransactionAndAwait) {
        coEvery { session.commitTransactionAndAwait() } just Runs

        runTest(TransactionMocks(client, session, block))
    }
}
