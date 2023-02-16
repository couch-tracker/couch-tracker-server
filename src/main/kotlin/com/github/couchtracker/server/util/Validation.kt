package com.github.couchtracker.server.util

sealed class ValidationResult {

    object Success : ValidationResult()

    data class Error(val message: String) : ValidationResult()

    fun require() {
        require(this is Success) {
            (this as Error).message
        }
    }
}

fun validate(condition: Boolean, message: () -> String): ValidationResult {
    return if (condition) {
        ValidationResult.Success
    } else {
        ValidationResult.Error(message())
    }
}

fun Collection<ValidationResult>.firstErrorOrSuccess(): ValidationResult {
    return firstOrNull { it is ValidationResult.Error } ?: ValidationResult.Success
}

class ValidationContext {

    private val results = mutableListOf<ValidationResult>()

    fun validate(condition: Boolean, message: () -> String) {
        results.add(com.github.couchtracker.server.util.validate(condition, message))
    }

    fun result() = results.firstErrorOrSuccess()
}

fun runValidations(run: ValidationContext.() -> Unit): ValidationResult {
    return ValidationContext().apply(run).result()
}
