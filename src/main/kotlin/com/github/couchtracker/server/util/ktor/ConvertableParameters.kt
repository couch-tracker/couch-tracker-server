package com.github.couchtracker.server.util.ktor

import io.ktor.http.Parameters
import io.ktor.server.application.ApplicationCall
import io.ktor.server.plugins.MissingRequestParameterException
import io.ktor.server.plugins.ParameterConversionException
import io.ktor.server.plugins.dataconversion.conversionService
import io.ktor.util.converters.ConversionService
import io.ktor.util.converters.DefaultConversionService
import io.ktor.util.reflect.typeInfo
import kotlin.reflect.KProperty

/**
 * This class allows to have a custom [conversionService] for some [parameters] instead of using the [DefaultConversionService].
 *
 * To use with query params, see [queryParams] extension property.
 *
 * Copied and adapted from https://youtrack.jetbrains.com/issue/KTOR-5384
 */
class ConvertableParameters(val parameters: Parameters, val conversionService: ConversionService) {

    inline operator fun <reified R> getValue(thisRef: Any?, property: KProperty<*>): R {
        val values = parameters.getAll(property.name)?.filter { it.isNotBlank() }
        val typeInfo = typeInfo<R>()

        if (values.isNullOrEmpty()) {
            when (typeInfo.kotlinType?.isMarkedNullable) {
                true -> return null as R
                false -> throw MissingRequestParameterException(property.name)
                null -> error("Unable to get kotlin type from type info")
            }
        }

        return try {
            conversionService.fromValues(values, typeInfo) as R
        } catch (expected: Exception) {
            throw ParameterConversionException(property.name, typeInfo.type.simpleName ?: typeInfo.type.toString(), expected)
        }
    }
}

/**
 * Returns an instance of [ConvertableParameters] to be used with the query parameters using the application's conversion service.
 *
 * Usage example:
 * ```
 * get { url ->
 *     val locales: List<Locale> by call.queryParams
 * }
 * ```
 *
 * Register converters in the application:
 * ```
 * install(DataConversion) {
 *     convert<Type> {
 *         decode { ... }
 *         encode { ... }
 *     }
 * }
 * ```
 *
 * See [DataConversion docs](https://ktor.io/docs/data-conversion.html)
 */
val ApplicationCall.queryParams get() = ConvertableParameters(request.queryParameters, application.conversionService)
