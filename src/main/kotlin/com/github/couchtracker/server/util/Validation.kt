package com.github.couchtracker.server.util

import com.github.couchtracker.server.util.ValidationResult.Error.Multiple
import com.github.couchtracker.server.util.ValidationResult.Error.Single
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

/**
 * Sealed class that represents the result of a validation
 */
@OptIn(ExperimentalContracts::class)
sealed class ValidationResult {

    /**
     * Represents a successful validation
     */
    object Success : ValidationResult()

    /**
     * Represents an error in validation.
     *
     * This class has two possible subclasses: [Single] and [Multiple], depending on the number of validation errors.
     */
    sealed class Error : ValidationResult() {

        abstract val message: String

        /**
         * Single validation error
         */
        data class Single(override val message: String) : Error()

        /**
         * Multiple validation errors
         *
         * One of the "child" errors can be [Multiple] instances as well, providing a tree hierarchy of errors.
         *
         * @param errors list of errors, must contain at least two elements
         */
        data class Multiple(val errors: List<Error>) : Error() {

            init {
                require(errors.size > 1)
            }

            /**
             * The message, generated using all the child messages
             */
            override val message: String
                get() = generateMessage()

            private fun generateMessage(depth: Int = 1): String {
                return errors.withIndex().joinToString(prefix = "${errors.size} validation errors:\n", separator = "\n") { (i, err) ->
                    buildString {
                        append("\t".repeat(depth))
                        append("${i + 1}. ")
                        append(
                            when (err) {
                                is Single -> err.message
                                is Multiple -> err.generateMessage(depth + 1)
                            },
                        )
                    }
                }
            }

            /**
             * Returns a new [Error.Multiple] with its errors flattened.
             *
             * @see [flatten]
             */
            fun flattened() = Multiple(errors.flatten())
        }
    }

    /**
     * Throws [IllegalArgumentException] iff this [ValidationResult] is an [Error]
     * @see [require]
     */
    fun require() {
        contract {
            returns() implies (this@ValidationResult is Success)
        }
        executeCheckFunction(::require)
    }

    /**
     * Throws [IllegalStateException] iff this [ValidationResult] is an [Error]
     * @see [check]
     */
    fun check() {
        contract {
            returns() implies (this@ValidationResult is Success)
        }
        executeCheckFunction(::check)
    }

    private fun executeCheckFunction(f: (Boolean, () -> String) -> Unit) {
        f(this is Success) {
            (this as Error).message
        }
    }
}

/**
 * Runs a single validation. Returns [ValidationResult.Success] if [condition] is true, otherwise returns [ValidationResult.Error.Single]
 * resolving provided [lazyMessage]
 */
fun validate(condition: Boolean, lazyMessage: () -> String): ValidationResult {
    return if (condition) {
        ValidationResult.Success
    } else {
        Single(lazyMessage())
    }
}

/**
 * Class that is used as a receiver to [runValidations] to collect a list of [ValidationResult]
 */
class ValidationContext {

    private val results = mutableListOf<ValidationResult>()

    /**
     * Run validation and [append] it to this [ValidationContext].
     * @see [com.github.couchtracker.server.util.validate]
     */
    fun validate(condition: Boolean, lazyMessage: () -> String): ValidationResult {
        return com.github.couchtracker.server.util.validate(condition, lazyMessage).also {
            append(it)
        }
    }

    /**
     *
     */
    fun runValidations(block: ValidationContext.() -> Unit): ValidationResult {
        return com.github.couchtracker.server.util.runValidations(block).also {
            append(it)
        }
    }

    /**
     * Appends the given [result] to this [ValidationContext]
     */
    fun append(result: ValidationResult) {
        results.add(result)
    }

    fun result() = results.coalesce()
}

/**
 * Utility function to run a series of validations and coalesce them into one.
 */
fun runValidations(run: ValidationContext.() -> Unit): ValidationResult {
    return ValidationContext().apply(run).result()
}

/**
 * Coalesces a collection of [ValidationResult] into a single one.
 *
 * If they are all [ValidationResult.Success], then [ValidationResult.Success] is returned.
 *
 * If there is a single [ValidationResult.Error], that single error is returned.
 *
 * If there are more [ValidationResult.Error]s, a new unflattened [ValidationResult.Error.Multiple] is created
 * @see [flatten]
 */
fun Collection<ValidationResult>.coalesce(): ValidationResult {
    val errors = filterIsInstance<ValidationResult.Error>()
    return when {
        errors.isEmpty() -> ValidationResult.Success
        errors.size == 1 -> errors.single()
        else -> Multiple(errors)
    }
}

/**
 * Flattens a collection of [ValidationResult.Error] into a list of [ValidationResult.Error.Single].
 * The given error hierarchy can be any deepness
 */
fun Collection<ValidationResult.Error>.flatten(): List<Single> {
    return flatMap {
        when (it) {
            is Single -> listOf(it)
            is Multiple -> it.errors.flatten()
        }
    }
}
