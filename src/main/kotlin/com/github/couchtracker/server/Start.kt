package com.github.couchtracker.server

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.UsageError
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option

object Start : CliktCommand(
    help = """
        Start one or more components of the app
        
        You can either specify --all to start all the components, or pass the components you want to run separated by a space.
        
        Example: couch-tracker-server start component1 component2
    """.trimIndent(),
    allowMultipleSubcommands = true,
    invokeWithoutSubcommand = true,
    printHelpOnEmptyArgs = true,
) {

    private val subcommands = listOf(HttpServer)

    init {
        subcommands(subcommands)
    }

    private val all by option("--all", help = "Starts all components").flag(default = false)

    override fun run() {
        if (all) {
            if (currentContext.invokedSubcommand != null) {
                throw UsageError("You can either pass --all or a list of components, but not both")
            }
            subcommands.forEach {
                it.run()
            }
        }
    }
}
