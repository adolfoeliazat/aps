package aps.back

import aps.*

// TODO:vgrechka Move shit from the top of shared-utils.kt

object bconst {
    val magick by lazy {sharedPlatform.getenv("APS_MAGICK") ?: "C:\\opt\\ImageMagick-7.0.4-Q16\\magick.exe"}
    val visualCaptureDir = "${const.file.APS_TEMP}/visual-capture"
    val tempBakDir = "${const.file.APS_TEMP}/bak"
    val ideaExe = "C:/opt/idea-2017/bin/idea64.exe"
//    val localRedisLoggingEnabled = System.getenv("APS_LOCAL_REDIS_LOGGING") == "true"

    object kvs {
        object test {
            val snapshotURL = "test:snapshotURL"
        }
    }
}


