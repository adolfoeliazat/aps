package aps.back

import aps.*
import into.kommon.*

// TODO:vgrechka Move shit from the top of shared-utils.kt

object bconst {
    val magick by lazy {getenv("APS_MAGICK") ?: "C:\\opt\\ImageMagick-7.0.4-Q16\\magick.exe"}
    val visualCaptureDir = "$APS_TEMP/visual-capture"
}

