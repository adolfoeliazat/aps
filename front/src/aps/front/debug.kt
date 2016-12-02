package aps.front

import aps.*

fun dumpShames() {
    for ((shame, ctrl) in TestGlobal.shameToControl) {
        clog("Shame: $shame")
        // clog(ctrl)
    }
}

