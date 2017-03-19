package aps

import kotlin.js.Date

val sharedPlatform = object : XSharedPlatform {
    override fun currentTimeMillis(): Long = Date().getTime().toLong()
    override fun getenv(name: String): String? = aps.process.env[name]
}
