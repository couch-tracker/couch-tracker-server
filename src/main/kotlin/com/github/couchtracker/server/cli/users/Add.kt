package com.github.couchtracker.server.cli.users

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.PrintCompletionMessage
import com.github.ajalt.clikt.core.PrintMessage
import com.github.ajalt.clikt.parameters.options.convert
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.prompt
import com.github.ajalt.clikt.parameters.options.validate
import com.github.couchtracker.server.cli.failOnError
import com.github.couchtracker.server.cli.runBlockingCli
import com.github.couchtracker.server.model.db.UserDbo
import com.github.couchtracker.server.util.Email
import com.github.couchtracker.server.util.Password
import com.github.couchtracker.server.util.Username

object Add : CliktCommand(help = "Add a new user") {

    private val email by option(help = "Email of the user").convert { Email(it) }.prompt()
    private val username by option(help = "Username of the user").convert { Username(it) }.prompt()
    private val password by option(help = "Password for the user")
        .convert { Password(it) }
        .prompt(hideInput = true, requireConfirmation = true)
        .validate { it.validate().failOnError(this) }
    private val name by option(help = "Name of the user").prompt(default = "")

    override fun run() = runBlockingCli { ad ->
        val inserted = UserDbo.insert(
            applicationData = ad,
            email = email,
            username = username,
            password = password,
            name = name,
        )
        if (inserted) {
            throw PrintCompletionMessage("User has been created", false)
        } else {
            throw PrintMessage("User with same email or username already exists", true)
        }
    }
}
