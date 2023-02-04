package com.github.couchtracker.server.common

import com.github.couchtracker.server.common.serializers.RegexSerializer
import kotlinx.serialization.Serializable
import kotlin.math.roundToLong

@Serializable(with = ByteSize.Serializer::class)
data class ByteSize(val value: Long, val unit: ByteUnit) {

    val bytes = value * unit.multiplier

    init {
        require(value >= 0)
    }

    fun convert(unit: ByteUnit) = ByteSize(
        value = value * (this.unit.multiplier / unit.multiplier.toDouble()).roundToLong(),
        unit = unit
    )

    operator fun compareTo(another: ByteSize) = bytes.compareTo(another.bytes)

    override fun toString() = "$value ${unit.symbol}"

    object Serializer : RegexSerializer<ByteSize>(
        name = "ByteSize",
        regex = "(\\d+)\\s*([kMG]?i?)B".toRegex(RegexOption.IGNORE_CASE),
        deserialize = {
            val (_, value, unit) = it.groupValues
            ByteSize(
                value = value.toLong(),
                unit = when (unit) {
                    "" -> ByteUnit.NONE
                    else -> ByteUnit.valueOf(unit.uppercase())
                }
            )
        }
    )
}

enum class ByteUnit(
    val symbol: String,
    val multiplier: Long,
) {
    NONE("B", 1),
    K("kB", 1000),
    KI("kiB", 1024),
    M("MB", 1000 * K.multiplier),
    MI("MiB", 1024 * KI.multiplier),
    G("GB", 1000 * M.multiplier),
    GI("GiB", 1024 * MI.multiplier);
}

val Long.bytes get() = ByteSize(this, ByteUnit.NONE)
val Long.kB get() = ByteSize(this, ByteUnit.K)
val Long.kiB get() = ByteSize(this, ByteUnit.KI)
val Long.MB get() = ByteSize(this, ByteUnit.M)
val Long.MiB get() = ByteSize(this, ByteUnit.MI)
val Long.GB get() = ByteSize(this, ByteUnit.G)
val Long.GiB get() = ByteSize(this, ByteUnit.GI)
