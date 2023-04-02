package com.github.couchtracker.server.model

import com.github.couchtracker.server.model.common.Resolution
import com.github.couchtracker.server.model.common.ResolutionClass.EIGHT_K
import com.github.couchtracker.server.model.common.ResolutionClass.FHD
import com.github.couchtracker.server.model.common.ResolutionClass.FOUR_K
import com.github.couchtracker.server.model.common.ResolutionClass.HD
import com.github.couchtracker.server.model.common.ResolutionClass.HQ
import com.github.couchtracker.server.model.common.ResolutionClass.SD
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe

class ResolutionTests : FunSpec(
    {

        context("Resolution.Size constructor should throw on invalid sizes") {
            withData(
                -10 to -10,
                0 to 0,
                100 to 0,
                0 to 100,
            ) { (width, height) ->
                shouldThrow<IllegalArgumentException> { Resolution.Size(width, height) }
            }
        }

        context("Resolution.Size#toClass() should map to the correct class") {
            withData(
                // SD
                Triple(400, 224, SD),
                Triple(512, 384, SD),
                Triple(624, 336, SD),
                Triple(640, 336, SD),
                Triple(688, 384, SD),
                Triple(720, 384, SD),
                Triple(720, 300, SD),
                Triple(720, 400, SD),

                // HQ
                Triple(720, 416, HQ),
                Triple(720, 576, HQ),
                Triple(720, 480, HQ),
                Triple(720, 540, HQ),
                Triple(640, 480, HQ),

                // HD
                // From: https://en.wikipedia.org/wiki/720p#Resolutions
                Triple(960, 720, HD),
                Triple(1280, 720, HD),
                // Other real life examples
                Triple(1248, 524, HD),
                Triple(1272, 692, HD),
                Triple(1280, 528, HD),
                Triple(1272, 692, HD),
                Triple(1280, 692, HD),

                // FHD
                // From: https://en.wikipedia.org/wiki/1080p#Resolutions
                Triple(1440, 1080, FHD),
                Triple(1620, 1080, FHD),
                Triple(1728, 1080, FHD),
                Triple(1920, 1080, FHD),
                Triple(2160, 1080, FHD),
                Triple(2400, 1080, FHD),
                Triple(2560, 1080, FHD),
                // Other real life examples
                Triple(1424, 1080, FHD),
                Triple(1536, 1072, FHD),
                Triple(1800, 1080, FHD),
                Triple(1902, 828, FHD),
                Triple(1904, 800, FHD),
                Triple(1920, 800, FHD),
                Triple(1920, 960, FHD),
                Triple(1920, 1036, FHD),
                Triple(1920, 1088, FHD),

                // 4K
                // From: https://en.wikipedia.org/wiki/4K_resolution#Resolutions
                Triple(3840, 1080, FOUR_K),
                Triple(3840, 1600, FOUR_K),
                Triple(3840, 2160, FOUR_K),
                Triple(3840, 2400, FOUR_K),
                Triple(3996, 2160, FOUR_K),
                Triple(4096, 1716, FOUR_K),
                Triple(4096, 2160, FOUR_K),
                Triple(4096, 2304, FOUR_K),
                Triple(4096, 2560, FOUR_K),
                Triple(4096, 3072, FOUR_K),
                // Other real life examples
                Triple(3840, 1396, FOUR_K),
                Triple(3840, 1608, FOUR_K),
                Triple(3584, 2160, FOUR_K),

                // 8K
                // From: https://en.wikipedia.org/wiki/8K_resolution#Resolutions
                Triple(7680, 2160, EIGHT_K),
                Triple(7680, 2400, EIGHT_K),
                Triple(7680, 3200, EIGHT_K),
                Triple(7680, 3240, EIGHT_K),
                Triple(7680, 4320, EIGHT_K),
                Triple(8192, 4320, EIGHT_K),
                Triple(8192, 4608, EIGHT_K),
                Triple(8192, 5120, EIGHT_K),
                Triple(8192, 8192, EIGHT_K),

                // Invalid
                Triple(10000, 10000, null),
            ) { (width, height, expectedClass) ->
                Resolution.Size(width, height).toClass()?.enum shouldBe expectedClass
            }
        }
    },
)
