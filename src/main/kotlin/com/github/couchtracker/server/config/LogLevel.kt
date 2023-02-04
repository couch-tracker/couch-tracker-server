package com.github.couchtracker.server.config

import ch.qos.logback.classic.Level


enum class LogLevel(val level: Level) {
    TRACE(Level.TRACE),
    DEBUG(Level.DEBUG),
    INFO(Level.INFO),
    WARN(Level.WARN),
    ERROR(Level.ERROR),
    OFF(Level.OFF)
} 