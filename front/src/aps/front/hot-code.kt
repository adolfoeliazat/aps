/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.front

import aps.*
import aps.front.Globus.realTypedStorageLocal
import kotlin.browser.window

class InitAutoReload {
    lateinit var initialCtime: String

    init {
        fun inita(): Promisoid<Unit> = async {
            if (Globus.mode != Mode.DEBUG) return@async

            initialCtime = mirandaGetSoftwareVersion().ctime
            schedule()
        }
        inita()
    }

    private fun schedule() {
        realBrowseroid.timer.setTimeout({tick()}, 500)
    }

    private fun tick() = async {
        if (initialCtime != mirandaGetSoftwareVersion().ctime) {
            if (realTypedStorageLocal.reloadTest) {
                TestGlobal.lastTestHrefMaybe?.let {
                    Globus.realLocation.href = it
                    return@async
                }
            }

            Globus.realLocation.reload()
        } else {
            schedule()
        }
    }
}

