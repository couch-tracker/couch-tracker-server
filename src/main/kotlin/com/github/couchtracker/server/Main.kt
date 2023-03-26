package com.github.couchtracker.server

import com.github.ajalt.clikt.core.NoOpCliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.github.couchtracker.server.cli.Cli
import mu.KotlinLogging

val logger = KotlinLogging.logger { }

object Main : NoOpCliktCommand(printHelpOnEmptyArgs = true) {

    init {
        subcommands(Start)
        subcommands(Cli)
    }
}

fun main(args: Array<String>) = Main.main(args)
