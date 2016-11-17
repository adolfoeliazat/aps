package aps.front

@JsName("global")
@native object ExternalGlobus {
    fun displayInitialShit(): Unit = noImpl
    fun makeSignInNavbarLinkVisible(): Unit = noImpl
}

object Globus {
    var lastAttemptedRPCName: String? = null
}



