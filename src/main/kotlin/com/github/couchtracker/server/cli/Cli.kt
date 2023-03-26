package com.github.couchtracker.server.cli

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import com.github.ajalt.clikt.core.NoOpCliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.options.OptionTransformContext
import com.github.couchtracker.server.ApplicationData
import com.github.couchtracker.server.cli.users.Users
import com.github.couchtracker.server.config.Config
import com.github.couchtracker.server.util.ValidationResult
import org.slf4j.LoggerFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking

object Cli : NoOpCliktCommand(
    help = "CLI to perform some administrative actions",
    printHelpOnEmptyArgs = true,
    invokeWithoutSubcommand = true,
) {

    init {
        subcommands(Users)
    }
}

fun runBlockingCli(block: suspend CoroutineScope.(ApplicationData) -> Unit) = runBlocking {
    val config = Config.load()
    // For CLI commands, we don't want the logs
    (LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME) as Logger).level = Level.OFF

    val ad = ApplicationData.create(this, config)
    block(ad)
}

fun ValidationResult.failOnError(context: OptionTransformContext) {
    when (this) {
        is ValidationResult.Error -> context.fail(this.message)
        is ValidationResult.Success -> Unit
    }
}
