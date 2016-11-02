/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

package aps.front

import aps.*
import into.kommon.*

object SpikeJS {
    @JsName("runShit")
    fun runShit() {
        testJSArrayInterop()
        clog("COOL")
    }

    fun testJSArrayInterop() {
        val jsShit = js("""[
            {name: 'wilma', age: 50},
            {name: 'fred', age: 30},
            {name: 'barney', age: 40},
        ]""")

        val kotlinShit: Array<dynamic> = jsShit
        clog("Before:\n", kotlinShit)
        kotlinShit.sortBy {it.age}
        clog("After:\n", kotlinShit)
    }
}
