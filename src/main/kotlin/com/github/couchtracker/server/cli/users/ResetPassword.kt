package com.github.couchtracker.server.cli.users

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.convert
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.prompt
import com.github.ajalt.clikt.parameters.options.transformAll
import com.github.couchtracker.server.cli.failOnError
import com.github.couchtracker.server.cli.runBlockingCli
import com.github.couchtracker.server.config.Config
import com.github.couchtracker.server.util.Password
import com.github.couchtracker.server.util.Username

class ResetPassword(val config: Config) : CliktCommand(help = "Reset the password for a user") {

    val user by option(help = "Email or username of the user").convert { Username(it) }.prompt()

    val password by option(help = "New password for the user. If not provided, it will be automatically generated")
        .transformAll { invocations ->
            when (val provided = invocations.lastOrNull()) {
                null -> {
                    val generated = "Passowrd123!"
                    prompt("Password", requireConfirmation = true, default = generated) { password ->
                        Password(password).also {
                            it.validate().failOnError(this)
                        }
                    }
                }
                else -> Password(provided).also {
                    it.validate().failOnError(this)
                }
            }
        }

    val invalidateTokens by option(help = "Weather to invalidate all login tokens or not")
        .flag("--dont-invalidate-tokens", "--keep-logged-in")

    override fun run() = runBlockingCli(config) {
        println("User: $user, Password: $password")
    }
}
