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
        fun inita(): Promise<Unit> = async {
            if (Globus.mode != Mode.DEBUG) return@async

            initialCtime = await(GetSoftwareVersionRequest.send()).ctime
            schedule()
        }
        inita()
    }

    private fun schedule() {
        window.setTimeout({tick()}, 500)
    }

    private fun tick() {"__async"
        if (initialCtime != __await(GetSoftwareVersionRequest.send()).ctime) {
            if (realTypedStorageLocal.reloadTest) {
                TestGlobal.lastTestHrefMaybe?.let {
                    window.location.href = it
                    return
                }
            }

            window.location.reload()

//            var href = window.location.href
//                .replace(Regex("#.*$"), "") // Otherwise it doesn't actually reload page
//            if (realTypedStorageLocal.reloadTest) TestShit.lastTestHref?.let {href = it}
//
//            window.location.href = href
        } else {
            schedule()
        }
    }
}

