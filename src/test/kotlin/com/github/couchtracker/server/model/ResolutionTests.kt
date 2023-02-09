package com.github.couchtracker.server.model

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import kotlin.test.assertEquals

class ResolutionTests {

    @Test
    fun testInvalidResolutionSize() {
        assertThrows<IllegalArgumentException> { Resolution.Size(-10, -10) }
        assertThrows<IllegalArgumentException> { Resolution.Size(0, 0) }
        assertThrows<IllegalArgumentException> { Resolution.Size(100, 0) }
        assertThrows<IllegalArgumentException> { Resolution.Size(0, 100) }
    }

    @ParameterizedTest
    @CsvSource(
        // SD
        " 400, 224, sd",
        " 512, 384, sd",
        " 624, 336, sd",
        " 640, 336, sd",
        " 688, 384, sd",
        " 720, 384, sd",
        " 720, 300, sd",
        " 720, 400, sd",

        // HQ
        " 720,  416, hq",
        " 720,  576, hq",
        " 720,  480, hq",
        " 720,  540, hq",
        " 640,  480, hq",

        // HD
        // From: https://en.wikipedia.org/wiki/720p#Resolutions
        " 960, 720, hd",
        "1280, 720, hd",
        // Other real life examples
        "1248,  524, hd",
        "1272,  692, hd",
        "1280,  528, hd",
        "1272,  692, hd",
        "1280,  692, hd",

        // FHD
        // From: https://en.wikipedia.org/wiki/1080p#Resolutions
        "1440, 1080, fhd",
        "1620, 1080, fhd",
        "1728, 1080, fhd",
        "1920, 1080, fhd",
        "2160, 1080, fhd",
        "2400, 1080, fhd",
        "2560, 1080, fhd",
        // Other real life examples
        "1424, 1080, fhd",
        "1536, 1072, fhd",
        "1800, 1080, fhd",
        "1902,  828, fhd",
        "1904,  800, fhd",
        "1920,  800, fhd",
        "1920,  960, fhd",
        "1920, 1036, fhd",
        "1920, 1088, fhd",

        // 4K
        // From: https://en.wikipedia.org/wiki/4K_resolution#Resolutions
        "3840, 1080, 4k",
        "3840, 1600, 4k",
        "3840, 2160, 4k",
        "3840, 2400, 4k",
        "3996, 2160, 4k",
        "4096, 1716, 4k",
        "4096, 2160, 4k",
        "4096, 2304, 4k",
        "4096, 2560, 4k",
        "4096, 3072, 4k",
        // Other real life examples
        "3840, 1396, 4k",
        "3840, 1608, 4k",
        "3584, 2160, 4k",

        // 8K
        // From: https://en.wikipedia.org/wiki/8K_resolution#Resolutions
        "7680, 2160, 8k",
        "7680, 2400, 8k",
        "7680, 3200, 8k",
        "7680, 3240, 8k",
        "7680, 4320, 8k",
        "8192, 4320, 8k",
        "8192, 4608, 8k",
        "8192, 5120, 8k",
        "8192, 8192, 8k",

        // Invalid
        "10000, 10000, null",
        nullValues = ["null"]
    )
    fun testResolutionSizeToClass(width: Int, height: Int, expected: String?) {
        val expectedClass =
            if (expected == null) {
                null
            } else {
                Resolution.Class(ResolutionClass.values().single { it.id == expected })
            }
        assertEquals(expectedClass, Resolution.Size(width, height).toClass())
    }
}
