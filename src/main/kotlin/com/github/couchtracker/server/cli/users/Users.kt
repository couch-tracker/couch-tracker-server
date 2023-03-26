package com.github.couchtracker.server.cli.users

import com.github.ajalt.clikt.core.NoOpCliktCommand
import com.github.ajalt.clikt.core.subcommands

object Users : NoOpCliktCommand(help = "Manage users", printHelpOnEmptyArgs = true) {

    init {
        subcommands(Add)
    }
}
